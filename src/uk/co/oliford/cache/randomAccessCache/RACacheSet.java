package uk.co.oliford.cache.randomAccessCache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import uk.co.oliford.jolu.OneLiners;
import uk.co.oliford.jolu.SettingsManager;


/** Managed a cache primary set file pair on disk, a ".mrc" for Minerva Random Cache"
 * 
 * An index of the file is loaded from disk on construction.
 * At first, the memory index contains only the request hashes and the
 *  position in the file of each entry.
 *  
 * When a request is made, every request key that hits the hash has to be loaded
 * from disk and checked properly. They then stay in the memory index on a weak reference.
 * 
 * If one matches properly, the data is loaded from disk and also kept on a weak reference. 
 * 
 * Loaded objects are left in memory on a weak reference, so will hopefully get unloaded 
 * by the VM as we run out of memory, as we can always pick them back up off the disk 
 * (and maybe even the OS's disk cache) later on. 
 * 
 * Unfortunately, the mixture of needing all of HashMap's internal logic that I cannot
 * be bothered to copy or rewrite, and needing to store the full keys on disk, means
 * we use a hashmap as map of entries key'ed by another hash. 
 * 
 * Multiple entries under a single hash are chained under RACacheEntry.nextEntry
 * because HashMap's internal logic for this isn't exposed to us.
 * 
 * 
 * The file looks something like this...
 * 
 * Strings are always an integer number of bytes, then a UTF8 char string. 
 *   
 * 	1) Long integer number of entries - that is the number of entries in the file not necc. the 
 *     number of entries in the cache, since they can overload each other.
 *     
 * 	2) Long integer random UUID. This is maintained for as long as the file is only appended to.
 *     If something cleans out the old entries, then it must reset this so that
 *     anyone else accessing the file knows to invalidate its file positions, or 
 *     reload/ignore the cache or something like that.  

 * 	3) String identifier, always "MinervaRandomAccessCacheVer" and a then a version integer.
 * 	  This is because I'm getting annoyed with not being able to tell what files are.
 *   
 * 	4) String cache type name.
 * 	5) String cache set name.
 * 	6) String cache tag name.
 * 
 * 
 * For each Entry:
 * 	a) x1 Entry code. One of ENTRYCODE_xxxx
 *  b) x4 integer hash of the request key
 *  c) x4 integer size of request key serialised package
 *  d) x4 integer size of cached object serialised package
 * 	e) x? Serialised request key package
 * 	f) x? Serialised cache object package.
 * 
 * Serialised object packages are an integer number of bytes and then the data
 * as per the normal object serialisation.
 * 
 * Individual objects cannot be over 4GB, that's (at the time of writing) just silly.
 * but the cache files can be.
 * 
 * @author oliford codes@oliford.co.uk
 *
 */
public class RACacheSet {
	public static final String IDSTR = "MinervaRandomAccessCacheVer";
	
	private final int userAttentionSpan = Integer.parseInt(SettingsManager.defaultGlobal().getProperty("minerva.user.attentionSpanMilisecs", "5000")); 
	
	/** If set, keeps this set will keep the file lock and not flush.
	 * Must faster like this, but can only be accessed here */
	private boolean singleAccess = false;
	
	/** A valid entry */
	public static final byte ENTRYCODE_VALID = 0x33;
	/** An entry that is superceeded by one later in the file. */
	public static final byte ENTRYCODE_OVERWRITTEN = 0x34;
	/** An entry that was specifically deleted from the cache. */
	public static final byte ENTRYCODE_DETELED = 0x35;
	/** Marker for end of file (useful because we I don't know how to shorten the file) */
	public static final byte ENTRYCODE_EOF = 0x36;
	
	/** Probably max header size (for locking and page mapping etc) */
	public static final int maxHeaderSize = 1024;
	
	/** The cache version, to check in the file */
	public static final int version = 1;
	
	/** IDs for this object, used by outside via the setters/getters*/
	private String cacheName;	
	private String setName;	
	private String tagName;	
	private String fileName;  
	
	private RandomAccessFile raFile;
	
	/** The file (NIO), random access and kept open */
	private FileChannel fc;
	
	private FileLock writeLock = null;
	
	@SuppressWarnings("unused")
	private String writeLockHolderStackTrace = "";
	
	/** Keep the first 16 bytes mapped, so we can tell when
	 * the file has been modified, hopefully without wearing the heads
	 * on the disk out. */
	private MappedByteBuffer headerMBBuf;
	
	/** The number of entries in the file as far as we are aware.
	 * If the number in the file gets bigger and the UUID remains the same
	 * we know that someone else has written to disk in the meantime and
	 * can simply load those extra entries. */
	private long nEntriesInFile;
		
	/** This UUID should match the one in the file. If it doesn't, then 
	 * the file has been reorganised or rewritten and we can't use it anymore. */
	private long fileUUID;
	
	/** The position of the end of the file last time we read or appended stuff.
	 * New stuff will be written here. It is assume to be correct for as long
	 * as nEntriesInFile doesn't change */
	private long lastKnownEndPos; 
	
	/** HashMap of memory cache. Keys in this are integer hashes of the real keys. */
	java.util.HashMap<Integer, RACacheEntry> memCache = new HashMap<>();
	
	/** Cache statistics */
	private static long nCollisionInPut = 0;
	private static long nMisses = 0;
	private static long nHitFromMem = 0;
	private static long nHitFromDisk = 0;
	private static long nKeyLostFromMem = 0;
	private static long nObjectLostFromMem = 0;
	/** Number of attempts before alerting the user to poor memdisk rates */
	private long regularStatsInterval = Long.parseLong(SettingsManager.defaultGlobal().getProperty("minerva.cache.regularStatsInterval", "240000"));
	private long lastStatsOutput = (regularStatsInterval > 0) ? System.currentTimeMillis() : Long.MIN_VALUE;
	private double nObjectLossRateBeforeStatsOutput = Double.parseDouble(SettingsManager.defaultGlobal().getProperty("minerva.cache.maxMemLossRateBeforeWarning", "0.80"));
	private long lastStatsWarning = 0;
	
	protected RACacheSet(String cacheName, String setName, String tagName) {
		this.cacheName = cacheName;
		this.setName = setName;
		this.tagName = tagName;		 
		this.fileName = RACacheService.getCachePath() + RACacheService.forFilename(cacheName) + //FIXME: Removed file seperator symbol between first two terms. If this breaks something we need to find a different solution. luru@ipp.mpg.de  
									"/minervaCache_" + RACacheService.forFilename(cacheName) + 
									"_" + RACacheService.forFilename(setName) +
									(tagName != null ? ("_" + RACacheService.forFilename(tagName)) : "") + ".mrc";
		System.out.println("filename in constructor is : "+this.fileName);
		fc = null; //don't open the file until we are actually called
	}
	
	/** Look up the given object, load the data and return it */
	public Object get(Object requestKey){
		try{
			fastSync();
			
			//if(fc == null)
				//openFile();
			
			RACacheEntry raEntry = findTrueRequestKeyMatch(requestKey, false);
			
			if(raEntry == null){
				nMisses++;
				return null;
			}
			
			Object object = null;
			if(raEntry.objectRef != null){
				object = raEntry.objectRef.get();
				if(object == null)
					nObjectLostFromMem++;
			}
			
			//checkStatsOutput();
			
			//if it's not in memory (it got GC'ed, or never was loaded)
			if(object == null){
				try{
					object = loadCachedObject(raEntry);				
				}catch (Exception e) {
					System.err.println("ERROR loading object in cache file " + fileName + ". If the object package is corrupt, run cleanCache(true) to remove it. Error was: ");
					e.printStackTrace();
					return null;			
				}
				nHitFromDisk++;
			}else{
				nHitFromMem++;
			}
			
			return object; // we've got it, yippee!
			
		}catch(Exception e){ //cache should never throw errors, only show warnings
			System.err.println("RACACHE ERROR in get():");
			e.printStackTrace();
			return null;
		}
	}
	
	/** Finds the RACacheEntry that /actually/ matches the request key out
	 * of those (if any) that match the hash. This may involve loaded
	 * keys from disk.
	 * 
	 * @param requestKey	Key to look for exact match of.
	 * @param remove		If true, the match is removed/decoupled from the memory cache.
	 * @return
	 */
	private RACacheEntry findTrueRequestKeyMatch(Object requestKey, boolean remove){
		// Yes, I know it's odd, but I do really mean to hashmap a hash here
		int hash = Arrays.deepHashCode(new Object[]{ requestKey });
		RACacheEntry raEntry = memCache.get(hash);
		RACacheEntry lastEntry = null;
		
		//for each entry on the hash collision chain
		while(raEntry != null){
			
			Object fullKey = getFullKey(raEntry);
			
			if(Arrays.deepEquals(new Object[]{ requestKey }, new Object[]{ fullKey })) {
				if(remove){
					//remove the discovered entry from the hashmap, or the collision chain 
					if(lastEntry == null){
						memCache.put(hash, raEntry.nextEntry);
											
					}else{
						lastEntry.nextEntry = raEntry.nextEntry;
					}
					
					//decouple from chain, since we're return the object
					raEntry.nextEntry = null;	
				}
				
				//since we now lose the 'fullKey' hard ref
				//the GC could free the request key here, but we don't actually care
				//because the caller has the key they just asked for, which is the
				//same thing
				return raEntry;
			}
			
			lastEntry = raEntry;
			raEntry = raEntry.nextEntry;
		}

		//no matching entries
		return null;
	}
	

	private Object getFullKey(RACacheEntry raEntry) {
		//we need the key (as a hard reference)
		Object fullKey = null;
		if(raEntry.keyRef != null){
			fullKey = raEntry.keyRef.get();
			if(fullKey == null)
				nKeyLostFromMem++;
		}
		
		//if it's not in memory (it got GC'ed, or was never loaded)
		if(fullKey == null){
			try{
				fullKey = loadRequestKey(raEntry);	
			}catch (Exception e) {
				System.err.println("ERROR loading request key in cache file " + fileName + ". If the key object package is corrupt, run cleanCache(true) to remove it. Error was: ");
				System.err.println("Currently, we think the UUID is: " + fileUUID);
				
				e.printStackTrace();
				fullKey = null; //stay in, we might find another one			
			}
		}
		return fullKey;
	}

	public List<Object> getKeys() {
		if(fc == null)
			openFile();
	
		LinkedList<Object> allKeys = new LinkedList<>();
		
		for(Entry<Integer, RACacheEntry> setEntry : memCache.entrySet()){
			RACacheEntry raEntry = setEntry.getValue();
			while(raEntry != null) {
				Object fullKey = getFullKey(raEntry);
				allKeys.add(fullKey);
				raEntry = raEntry.nextEntry;
			}
		}
		
		return allKeys;
	}

	/** Add the given object to the cache */
	public void put(Object requestKey, Object object){
		try {
			if(fc == null)
				openFile();
		
			//all of this is going to have to be done with the file locked 
			writeLock();
			
			checkIfFileHasBeenExtended();

			//now see if we already know about this object (either we did already, 
			// or another process added it and we've just loaded that.
			// remove/decouple it if found
			removeExistingEntry(requestKey, true);
			
			byte keyData[] = objectToData(requestKey);
			byte objectData[] = objectToData(object);
			
			// we need to make a new entry
			RACacheEntry entry = new RACacheEntry();
			entry.entryHeaderPos = lastKnownEndPos;
			entry.nextEntry = null;
			entry.keyRef = new SoftReference<>(requestKey);
			entry.keyPackageSize = keyData.length;
			entry.objectRef = new SoftReference<>(object);
			entry.objectPackageSize = objectData.length;
			
			int hash = Arrays.deepHashCode(new Object[]{ requestKey });
			
			//add it to the memory cache first
			addEntryToMemoryCache(entry, hash);
			
			addToFileCache(hash, keyData, objectData);

			updateEntryCount();

			checkStatsOutput();
			
		} catch (Exception e) {
			//cache should never throw errors
			System.err.println("ERROR adding an entry to cache file " + fileName + ": ");
			e.printStackTrace();
		} finally {
			//and we're done, force it to disk and release the lock
			if(writeLock != null){
				try {
					flushAndRelease();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				System.err.println("RACacheSet.put() exiting with no file lock.");
			}
		}
		
		
	}
	
	private void writeLock()  throws IOException {
		long t0 = System.currentTimeMillis();
		long t = t0;
		
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		(new Throwable()).printStackTrace(printWriter);

		/**
		synchronized (this) {
			String stackTraceStr = result.toString();
			if (writeLock != null) {
				System.err.println("RACache writeLock() called with lock already held.\n"
						+ "writeLock(): Stack trace of first call:\n" + writeLockHolderStackTrace + "\n"
						+ "writeLock(): Stack trace of second call:\n" + stackTraceStr + "\n");
			} else {
				writeLockHolderStackTrace = stackTraceStr;
			}
		}
		*/
		
		if(singleAccess && writeLock != null) //we already have the lock
			return; 
		
		do{
			boolean isLockedHere = false;
			try{
				writeLock = fc.tryLock();
			}catch(OverlappingFileLockException e){
				isLockedHere = true;
			}
			try {
				Thread.sleep(0, 100);
			} catch (InterruptedException e) { }
			if((System.currentTimeMillis() - t) > userAttentionSpan){
				System.out.println("INFO: RandomAccessCache has been waiting for file lock for " + ((System.currentTimeMillis()-t0)/1000) + "s. isLockedHere="+isLockedHere);
				t = System.currentTimeMillis();
			}
		}while(writeLock == null);
	}
	
	private void flushAndRelease() throws IOException{
		if(singleAccess)
			return;
		
		fc.force(false);	// Not sure if we need this to make multiple access work	
		writeLock.release();
		writeLock = null;
	
	}
	
	/** Checks the header entry count to see if the file has grown or been rearranged.
	 * This doesn't do anything about it. For that, see checkIfFileHasBeenExtended() */
	private boolean hasFileChanged() throws IOException {
		//see if the file has been modified		
		headerMBBuf.rewind();
		long nEntriesHeader = headerMBBuf.getLong();
		long uuid = headerMBBuf.getLong();
		
		//if the UUID changed, the file has been totally reorganised, so we have no choice but to clear and reload
		return fileUUID != uuid || nEntriesHeader != nEntriesInFile;
	}
		
	/** Checks the header entry count to see if the file has grown and if so reads the addition, or re-reads everything */
	private void checkIfFileHasBeenExtended() throws IOException {
		//see if the file has been modified		
		headerMBBuf.rewind();
		long nEntriesHeader = headerMBBuf.getLong();
		long uuid = headerMBBuf.getLong();
		
		//if the UUID changed, the file has been totally reorganised, so we have no choice but to clear and reload
		if(fileUUID != uuid){
			System.out.println("RACache UUID changed from "+fileUUID+" to "+uuid+", reloading index");
			memCache.clear();						
			loadAll();
			
		}else if(nEntriesHeader != nEntriesInFile){
			
			loadRemainingIndex(nEntriesHeader);
		}
	}
	
	/** Search for and remove the given entry from both the memory and disk cache */
	private boolean removeExistingEntry(Object requestKey, boolean overwriting) throws IOException{
		RACacheEntry raEntry = findTrueRequestKeyMatch(requestKey, true);
		
		if(raEntry != null){
			// we need to invalidate that one
			fc.position(raEntry.entryHeaderPos);
			ByteBuffer bBuf = ByteBuffer.allocate(1); //yes, really, 1 byte
			bBuf.put(overwriting ? ENTRYCODE_OVERWRITTEN : ENTRYCODE_DETELED); //invalidate
			bBuf.flip();
			fc.write(bBuf);
			
			return true;
		}
		
		return false;
	}
	
	/** If an entry for the given key exists, delete it from disk and memory cache */
	public boolean delete(Object requestKey){
		try {
			return removeExistingEntry(requestKey, false);
		} catch (Exception e) {
			System.err.println("ERROR deleting an entry from cache file " + fileName + ": ");
			e.printStackTrace();
			return true;
		}
	}
	
	private void addToFileCache(int hash, byte keyData[], byte objectData[]) throws IOException{
		//and now to the file
		fc.position(lastKnownEndPos);
		ByteBuffer bBuf = ByteBuffer.allocate(13);		
		bBuf.put(ENTRYCODE_VALID);
		bBuf.putInt(hash);
		bBuf.putInt(keyData.length);
		bBuf.putInt(objectData.length);
		bBuf.flip();
		fc.write(bBuf);
		
		writeData(keyData, objectData);
		
		lastKnownEndPos += 13 + keyData.length + objectData.length;
		
		writeEOFMarker(lastKnownEndPos);

		//and change the entry count
		nEntriesInFile++;
		
	}
	
	private void writeData(byte keyData[], byte objectData[]) throws IOException{
		writeDataA(keyData);
		writeDataA(objectData);
		/*ByteBuffer bBuf = ByteBuffer.wrap(keyData);
		fc.write(bBuf);
		
		bBuf = ByteBuffer.wrap(objectData);
		fc.write(bBuf);*/
	}
	
	private void writeDataA(byte data[]) throws IOException{
		ByteBuffer bBuf = ByteBuffer.wrap(data);
		writeDataB(bBuf);
	}
	

	private void writeDataB(ByteBuffer bBuf) throws IOException{
		fc.write(bBuf);
	}
	
	private void updateEntryCount() throws IOException{
		//fc.position(0);
		//ByteBuffer bBuf = ByteBuffer.allocate(8);
		headerMBBuf.rewind();
		headerMBBuf.putLong(nEntriesInFile); //invalidate
		//bBuf.flip();
		//fc.write(bBuf);*/
		
	}
	
	
	private void openFile(){
		try {
			//with plenty of time, we can make sure the directory exists
			OneLiners.mkdir(fileName);
			
			// now we need to open/create the file and lock it ASAP.
			// Maybe there is some nastiness possibility here if two 
			// programs try to do this at the same time. We really want
			// to create/open and lock the file in one go
			File aFile = new File(fileName);
			raFile = new RandomAccessFile(aFile, "rw");
			fc = raFile.getChannel();
			
			//grab a complete lock on the whole file, because we need to know whether
			//to read or create the header, and we don't want the index
			// changing size as we're reading it
			writeLock(); 
			
			//ok, now we have some time we check to see if there is a header (i.e already a file)
			//and make it if not
			
			if(fc.size() > 0)
				loadAll();
			else
				createHeader();

			
		} catch (Exception e) {
			e.printStackTrace();
			fc = null;
		} finally {
			if(writeLock != null){
				try {
					flushAndRelease();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				System.err.println("RACacheSet.openFile() exiting with no file lock.");
			}
		}
	}

	private Object loadRequestKey(RACacheEntry raEntry) throws IOException, ClassNotFoundException{
		Object key = loadSerialisedObject(
				raEntry.entryHeaderPos + 13,
				raEntry.keyPackageSize );
		raEntry.keyRef = new SoftReference<>(key);
		return key;
	}

	private Object loadCachedObject(RACacheEntry raEntry) throws IOException, ClassNotFoundException{
		Object obj = loadSerialisedObject(
				raEntry.entryHeaderPos + 13 + raEntry.keyPackageSize,
				raEntry.objectPackageSize );
		raEntry.objectRef = new SoftReference<>(obj);
		return obj;
	}
	
	
	private Object loadSerialisedObject(long startPos, int size) throws IOException, ClassNotFoundException{
		//TODO: Try with the non-mapped IO
		//MappedByteBuffer mbBuf = fc.map(MapMode.READ_ONLY, startPos, size);
		fc.position(startPos);
		ByteBuffer bBuf = ByteBuffer.allocate(size);
		fc.read(bBuf);
		
		//get the array of the data
		byte data[] = bBuf.array();
		//the javaDoc says may have to actually read it 
		if(data == null){ 
			data = new byte[size];
			bBuf.get(data);
		}

		return dataToObject(data);
			
	}
	
	private static final Object dataToObject(byte data[]) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);		
		ObjectInputStream oin = new ObjectInputStream(bin);
		return oin.readObject();
	}
	
	private static final byte[] objectToData(Object obj) throws IOException {
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(bout);
		oout.writeObject(obj);
		
		return bout.toByteArray();
	}
	
	private void loadAll() throws IOException{
		headerMBBuf = fc.map(MapMode.READ_WRITE, 0, 16);
		
		long nEntriesHeader = headerMBBuf.getLong();
		this.fileUUID = headerMBBuf.getLong();
		
		System.out.println("RACache loadAll() reading "+nEntriesHeader+" entries from '"+fileName+"' with UUID " + fileUUID);
//		fileName="C:\MINERVA\Cache\magconf
//		System.out.println("RACache loadAll() reading "+nEntriesHeader+" entries from '"+fileName+"' with UUID " + fileUUID);
		
		fc.position(16);
		ByteBuffer bBuf = ByteBuffer.allocate(maxHeaderSize);
		fc.read(bBuf);
		bBuf.flip();
		
		String id = readString(bBuf);
		String fileCacheName = readString(bBuf);
		String fileSetName = readString(bBuf);
		String fileTagName = readString(bBuf);
		
		if(!id.startsWith(IDSTR))
			throw new RuntimeException("File '"+fileName+"' is not a minerva random access cache file (doesn't start with '"+ IDSTR +"')");
		
		int ver = Integer.parseInt(id.substring(IDSTR.length()));
		
		if(ver != RACacheSet.version) 
			//TODO: With this, we will probably want to convert the file to the new version, or something like that?
			throw new RuntimeException("Cache file '"+fileName+"' is version "+ver+" but this code is version "+version);
		
		if(!fileCacheName.equals(cacheName))
			System.err.println("WARNING: Cache file internal type '"+fileCacheName+"' doesn't match fileName ''"+fileName+"'.");
		if(!fileSetName.equals(setName))
			System.err.println("WARNING: Cache file internal set name '"+fileSetName+"' doesn't match fileName ''"+fileName+"'.");
		if( !(fileTagName == null && tagName == null) && 
					(fileTagName == null || !fileTagName.equals(tagName) ) )
			System.err.println("WARNING: Cache file internal tag '"+fileTagName+"' doesn't match fileName ''"+fileName+"'.");

		if(!memCache.isEmpty()) //sanity check
			System.err.println("ERROR: loadAll() called when memCache is not empty.");
		
		 lastKnownEndPos = 16 + bBuf.position();
		 nEntriesInFile = 0;
		 loadRemainingIndex(nEntriesHeader);
		 
	}
	
	/** Loads any more index, from the last known position in the file.
	 * 
	 * Expects the number of entries to reach nEntriesHeader.
	 */
	private void loadRemainingIndex(long nEntriesHeader) throws IOException{
				
		 ByteBuffer bBuf = ByteBuffer.allocate(13); //17 bytes for the package info
		 
		 int nNewEntriesFound = 0;
		 do{
		 	 
			fc.position(lastKnownEndPos);
			

			if(fc.read(bBuf) <= 0)	//read the 13 bytes of info
				break;
				
			bBuf.flip();
			
			byte entryCode = bBuf.get();
			if(entryCode == ENTRYCODE_EOF)
				break;
			
			if(bBuf.limit() < 13){
				System.err.println("WARNING: loadRemainingIndex(): Cache entry header corrupt at position " + lastKnownEndPos + " in cache file " +fileName + ". Dropping all further items");
				break;
			}

			int hash = bBuf.getInt();
			int reqKeySize = bBuf.getInt();
			int objSize = bBuf.getInt();
			
			bBuf.flip();

			switch(entryCode){			
				case ENTRYCODE_VALID:
					RACacheEntry raEntry = new RACacheEntry();
					raEntry.entryHeaderPos = lastKnownEndPos;
					raEntry.nextEntry = null;
					raEntry.keyPackageSize = reqKeySize;
					raEntry.objectPackageSize = objSize;
					
					addEntryToMemoryCache(raEntry, hash);
					break;
				case ENTRYCODE_DETELED:
				case ENTRYCODE_OVERWRITTEN:
					break;
				default:
					nEntriesInFile += nNewEntriesFound;
					System.err.println("WARNING: Unrecognised entry code "+entryCode+" at position " + lastKnownEndPos +
										" in cache file " + fileName + ". Truncating file here at nEntries = " + nEntriesInFile);
					
					writeEOFMarker(lastKnownEndPos);
					updateEntryCount();
					return;
			}
				
			lastKnownEndPos += 13 + reqKeySize + objSize;
			
			nNewEntriesFound++;
		}while(true);
		
		if((nEntriesInFile + nNewEntriesFound) != nEntriesHeader)
			System.err.println("WARNING: loaded entries from '"+fileName+"': (" + nEntriesInFile + 
					" existing + " + nNewEntriesFound + " new = " + (nEntriesInFile + nNewEntriesFound) +
					" does not match header's claim of "+nEntriesHeader+" entries.");
	 	
		nEntriesInFile = (nEntriesInFile + nNewEntriesFound) ;
	}
	
	/** Puts a new RACacheEntry in memCache, coping with collisions */ 
	private void addEntryToMemoryCache(RACacheEntry raEntry, int hash){
		
		//see if something already exists under this 
		RACacheEntry collision = memCache.get(hash);
		if(collision != null){
			nCollisionInPut++;
			
			//chain this one on the end
			while(collision.nextEntry != null)
				collision = collision.nextEntry;
			
			collision.nextEntry = raEntry;
		}else{
			memCache.put(hash, raEntry);
		}
	}
	
	private void createHeader() throws IOException{
		headerMBBuf = fc.map(MapMode.READ_WRITE, 0, 16);
		
		nEntriesInFile = 0;
		//this isn't for numerics, we don't need a proper randGen here
		fileUUID = (new Random()).nextLong(); 
		
		headerMBBuf.rewind();
		headerMBBuf.putLong(nEntriesInFile);
		headerMBBuf.putLong(fileUUID);
		
		String verStr = IDSTR + version;
		
		int hdrLen = 16 + verStr.length() + cacheName.length() + setName.length() + (tagName != null ? tagName.length() : 0);
		ByteBuffer bBuf = ByteBuffer.allocate(hdrLen);
		
		fc.position(16);
		
		writeString(bBuf, verStr);
		writeString(bBuf, cacheName);
		writeString(bBuf, setName);
		writeString(bBuf, tagName);
		bBuf.flip();
		fc.write(bBuf);
		
		lastKnownEndPos = 16 + hdrLen;
		
		raFile.setLength(lastKnownEndPos);
		
	}
	
	private String readString(ByteBuffer bBuf){
		int len = bBuf.getInt();
		if (len < 0)
			return null;
		byte strBytes[] = new byte[len];
		bBuf.get(strBytes);
		return new String(strBytes);		
	}
	
	private void writeString(ByteBuffer bBuf, String str){
		if(str == null){
			bBuf.putInt(-1);
			return;
		}
		bBuf.putInt(str.length());
		bBuf.put(str.getBytes());
	}
	

	/** Check the UUID and number of entries in the cache file 
	 * and load any new ones into the file 
	 */
	public void fastSync(){
		if(fc == null)
			openFile();
		
		try{
			//aquiring the write lock is expensive, so just do an indicative check first
			if(!hasFileChanged())
				return;
		}catch(IOException e){
			System.err.println("ERROR during fastSync() of cache file " + fileName + ": ");
			e.printStackTrace();
		}
			
		try {
			//otherwise we need to get the write lock
			writeLock();
			
			checkIfFileHasBeenExtended();

		} catch (Exception e) {
			System.err.println("ERROR during sync of cache file " + fileName + ": ");
			e.printStackTrace();
		} finally {
			//and we're done, force it to disk and release the lock
			if(writeLock != null){
				try {
					flushAndRelease();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				System.err.println("RACacheSet.fastSync() exiting with no file lock.");
			}
		}
	}
	
	/** Deletes and overwrites of existing entries normally just tag the old ones
	 * as deleted and add the new one to the end of the file.  
	 * 
	 * This method completely rewrites the cache file, in place,
	 * clearing all deleted and overwritten items.
	 */ 
	public void cleanCache(boolean checkObjectPackageIntegrity) {
		
		System.out.println("RandomAccessCache: Cleaning cache file '"+fileName+"': ");
		
		try {
			long nEntriesKept = 0;
			long nEntriesRemoved = 0;
			long nBytesFreed = 0;
			
			if(fc == null){ 
				//in this particular case, we don't need to run openFile()
				//because we are about the parse the index anyway
				File aFile = new File(fileName);
				OneLiners.mkdir(aFile.getParentFile().toPath());
				raFile = new RandomAccessFile(aFile, "rws");
				fc = raFile.getChannel();
				headerMBBuf =null;
			}
						
			writeLock();
			
			if(headerMBBuf == null)
				headerMBBuf = fc.map(MapMode.READ_WRITE, 0, 16);
			
			headerMBBuf.rewind();
			long nEntriesHeader = headerMBBuf.getLong(); 
			long uuid = headerMBBuf.getLong();
			
			System.out.println("UUID was " + uuid);
			
			//read-re the header so we start at the beginning
			fc.position(16);
			ByteBuffer bBuf = ByteBuffer.allocate(maxHeaderSize);
			fc.read(bBuf);
			bBuf.flip();

			// not used at the moment, which is probably fine. we cannot delete, because this advances the buffer 
			@SuppressWarnings("unused")
			String id = readString(bBuf);
			@SuppressWarnings("unused")
			String fileCacheName = readString(bBuf);
			@SuppressWarnings("unused")
			String fileSetName = readString(bBuf);
			@SuppressWarnings("unused")
			String fileTagName = readString(bBuf);
			
			long headerEndPos = 16 + bBuf.position();
			long readPos = headerEndPos;
			long writePos = headerEndPos;
			
			bBuf = ByteBuffer.allocate(13);
			
			boolean breakWhile = false;
			do {
			 	 
				fc.position(readPos);
				
				if(fc.read(bBuf) <= 0)	//read the 13 bytes of info
					break;
					
				bBuf.flip();
				
				byte entryCode = bBuf.get();
				if(entryCode == ENTRYCODE_EOF)
					break;
				
				if (bBuf.limit() < 13) {
					System.err.println("WARNING: Cache entry header corrupt at position " + readPos + " in cache file "
							+ fileName + ". Dropping all further items");
					break;
				}

				@SuppressWarnings("unused")
				int hash = bBuf.getInt();

				int reqKeySize = bBuf.getInt();
				int objSize = bBuf.getInt();
				
				switch(entryCode){
					case ENTRYCODE_VALID:
						if(checkObjectPackageIntegrity){
							try{
								loadSerialisedObject(readPos + 13, reqKeySize);
								loadSerialisedObject(readPos + 13 + reqKeySize, objSize);
							}catch(Exception e){
								System.err.println("WARNING: Object package integrity failed for entry at position " + readPos + " in cache file " +fileName + ". Dropping this item ");
								//failed, so skip the read forward, but leave the write where it is
								readPos += 13 + reqKeySize + objSize;
								
								nEntriesRemoved++;
								nBytesFreed += 13 + reqKeySize + objSize;
								
								break;
							}
						}
						
						//valid, so we need to copy it
						if (writePos == readPos) {
							//if we are still reading and writing in the same place 
							//(i.e. nothing had been removed yet, just skip)
							readPos += 13 + reqKeySize + objSize;
							writePos = readPos;							
							nEntriesKept++;
							
						} else {
							//otherwise, read the data and write it back
							/*if(readPos > (writePos + 13 + reqKeySize + objSize))
								shiftEntryFast(bBuf, readPos, writePos, reqKeySize, objSize);
							else*/
								shiftEntrySlow(bBuf, readPos, writePos, reqKeySize, objSize);

							//both pointers move on
							readPos += 13 + reqKeySize + objSize;
							writePos += 13 + reqKeySize + objSize;
							nEntriesKept++;
						}
						break;
					default:
						System.err.println("WARNING: Unrecognised entry code "+entryCode+" at position " + readPos +
											" in cache file " + fileName + ". Truncating file here at nEntries = " + nEntriesKept);
						breakWhile = true; //we can't continue
						break;
						
					case ENTRYCODE_DETELED:
					case ENTRYCODE_OVERWRITTEN:
						//skip the read forward, but leave the write where it is
						readPos += 13 + reqKeySize + objSize;
						
						nEntriesRemoved++;
						nBytesFreed += 13 + reqKeySize + objSize;

						break;
				}
				
				long nEntriesParsed = (nEntriesKept + nEntriesRemoved);
				
				if( ((nEntriesParsed/(double)nEntriesHeader) % 0.01) < (((nEntriesParsed-1.0)/(double)nEntriesHeader) % 0.01) )
					System.out.print(".");
				
				if( ((nEntriesParsed/(double)nEntriesHeader) % 0.1) < (((nEntriesParsed-1.0)/(double)nEntriesHeader) % 0.1) )
					System.out.println((int)(100.0*(nEntriesKept + nEntriesRemoved)/(double)nEntriesHeader) + "% ");
				
				bBuf.flip();
				
			}while(!breakWhile);
			
			//write an EOF marker (but of course we won't move lastKnownEndPos past it)
			writeEOFMarker(writePos);
			
			raFile.setLength(writePos+1);
			 
			//write the new count and a new UUID
			fileUUID = (new Random()).nextLong(); 
			headerMBBuf.rewind();
			headerMBBuf.putLong(nEntriesKept);
			headerMBBuf.putLong(fileUUID);
			
			System.out.println("UUID now " + fileUUID);
			
			System.out.println("Cleaned cache file '" + fileName + "':\n" +
								"\tOriginal: "+nEntriesHeader+" reported, "+(nEntriesRemoved+nEntriesKept)+" found and "+readPos+" bytes read.\n" +
								"\tRemoved "+nEntriesRemoved+" entries and "+nBytesFreed+" bytes.\n" +
								"\tKept "+nEntriesKept+" entries and "+writePos+" bytes.");
			
			//write the new count to the header
			nEntriesInFile = nEntriesKept;
			updateEntryCount();
			
			//reset to start
			memCache.clear();
			loadAll();

			
		} catch (Exception e) {
			System.err.println("ERROR during clean of cache file " + fileName + ", file now in unknown state: ");
			e.printStackTrace();
			
			fc = null;
		} finally {
			//and we're done, force it to disk and release the lock
			if(writeLock != null){
				try {
					flushAndRelease();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				System.err.println("RACacheSet.cleanCache() exiting with no file lock.");
			}

			//close the file
			try {  fc.close(); }catch (Exception e) { e.printStackTrace(); }
			try {  raFile.close(); }catch (Exception e) { e.printStackTrace(); }
			fc = null; //mark it closed so we open again on next op
			raFile = null;
		}
	}

	/** Doesn't work, even if not overlapping */
	@SuppressWarnings("unused")
	private void shiftEntryFast(ByteBuffer bBuf, long readPos, long writePos, int reqKeySize, int objSize) throws IOException{
		fc.position(writePos);
		fc.transferFrom(fc, readPos, 13 + reqKeySize + objSize);
	}
	
	private void shiftEntrySlow(ByteBuffer bBuf, long readPos, long writePos, int reqKeySize, int objSize) throws IOException{
		fc.position(writePos);
		//header first
		bBuf.rewind();
		fc.write(bBuf);
		
		// we should be able to fit key and object in memory together
		ByteBuffer copyBuf = ByteBuffer.allocate(reqKeySize + objSize);
		fc.position(readPos + 13);
		fc.read(copyBuf);
		copyBuf.flip();
		fc.position(writePos + 13);
		fc.write(copyBuf);
		
		
	}
	
	private void writeEOFMarker(long writePos) throws IOException{
		ByteBuffer bBuf = ByteBuffer.allocate(1);
		writeEOFMarkerB(bBuf, writePos);
	}
	
	private void writeEOFMarkerB(ByteBuffer bBuf, long writePos) throws IOException{
		fc.position(writePos);
		bBuf.rewind();
		bBuf.put(ENTRYCODE_EOF);
		bBuf.flip();
		fc.write(bBuf);
	}
	
	public final String getCacheFileName() { return this.fileName; }	
	public final String getCacheType(){ return this.cacheName; };	
	public final String getSetName(){ return this.setName; };	
	public final String getTagName(){ return this.tagName; };	
	public final String getFileName(){ return this.fileName; };  
	
	private void checkStatsOutput(){
		//Check to see how the memory loss rate is going, and notify the user if its bad
		if((nObjectLostFromMem / (nHitFromDisk + nHitFromMem + 1.0)) > nObjectLossRateBeforeStatsOutput &&
				 (System.currentTimeMillis() - lastStatsWarning) > 5000){
			System.err.println("WARNING: RACache is loosing a lot of objects from memory, you probably want to increase the VM size");
			dumpCacheMemoryStats(System.err);

			lastStatsWarning = System.currentTimeMillis();
			lastStatsOutput = lastStatsWarning; //supress normal stats output since we've just displayed them	
		}
		
		if(regularStatsInterval > 0 && (System.currentTimeMillis() - lastStatsOutput) > regularStatsInterval){
			// Regular stats update
			dumpCacheMemoryStats(System.out);
			lastStatsOutput = System.currentTimeMillis();
		}
	}
	
	public void dumpCacheMemoryStats(){ dumpCacheMemoryStats(System.out); }
	
	public void dumpCacheMemoryStats(PrintStream out){
		long nEntries = 0;
		long nKeysInMem = 0;
		long nObjsInMem = 0;
		long bytesKeyPacksInMem = 0; //This doesn't really make sense, since the packages are not kept in mem.
		long bytesObjPacksInMem = 0; // but it gives a lower estimate of the memory used by the objects
		
		for(Entry<Integer, RACacheEntry> entry : memCache.entrySet()){
			RACacheEntry raEntry = entry.getValue();
			
			do{
				if(raEntry.keyRef != null && raEntry.keyRef.get() != null){
					bytesKeyPacksInMem += raEntry.keyPackageSize;
					nKeysInMem++;
				}

				if(raEntry.objectRef != null && raEntry.objectRef.get() != null){
					bytesObjPacksInMem += raEntry.objectPackageSize;
					nObjsInMem++;
				}
				
				nEntries++;
				
				raEntry = raEntry.nextEntry;
			}while(raEntry != null);
		}
		
		out.println("Statistics for RACache set cache='"+cacheName+"', set='"+setName+"', tag='"+tagName+"':");
		out.println("  Entries = " + nEntries);
		
		if(nEntries > 0){
			out.println("  nKeysInMem = " + nKeysInMem + " = " + ((100*nKeysInMem)/nEntries) + 
					"% = " + (bytesKeyPacksInMem/1024) + " kB (very approx)");
			out.println("  nObjsInMem = " + nObjsInMem + " = " + ((100*nObjsInMem)/nEntries) + 
					"% = " + (bytesObjPacksInMem/1024) + " kB (very approx)");
		}
		
		long nAttempts = nMisses + nHitFromMem + nHitFromDisk;
		out.println("  nAttempts = " + nAttempts);
		if(nAttempts > 0){
			out.println("  nMisses = " + nMisses + " = " + ((100*nMisses)/nAttempts) + "% ");
			out.println("  nHitFromMem = " + nHitFromMem + " = " + ((100*nHitFromMem)/nAttempts) + "% ");
			out.println("  nHitFromDisk = " + nHitFromDisk + " = " + ((100*nHitFromDisk)/nAttempts) + "% ");
		}
		long nHits = nHitFromMem + nHitFromDisk;
		if(nHits > 0){
			out.println("  nKeyLostFromMem = " + nKeyLostFromMem + " = ~ " + ((100*nKeyLostFromMem)/(nHits)) + "% ");
			out.println("  nObjectLostFromMem = " + nObjectLostFromMem + " = " + ((100*nObjectLostFromMem)/(nHits)) + "% ");
		}
		
		out.println("nCollisionInPut = " + nCollisionInPut);
		
	}

	/** Empties and closes the file, then clears the memory cache.
	 * 
	 * This is about as close to deleting a cache set as we can get without
	 * breaking the simultaneous access.
	 */
	public void emptyFile() {
		try {
			if(fc == null){
				//we need to open the file to lock it, but we don't need to do
				//a  full index read
				File aFile = new File(fileName);
				raFile = new RandomAccessFile(aFile, "rw");
				fc = raFile.getChannel();
			}
			if(headerMBBuf == null)
				headerMBBuf = fc.map(MapMode.READ_WRITE, 0, 16);
			
			writeLock(); //make sure no one else is in operation
			
			createHeader();
			
			writeLock.release();
			writeLock = null;

			fc.close();
			raFile.close();
			
			fc = null;
			raFile = null;
			
			memCache.clear();
			
		} catch (Exception e) {
			System.err.println("ERROR: Couldn't delete cache file '"+fileName+"':");
			e.printStackTrace();
		} finally {
			if(writeLock != null){
				try {
					writeLock.release();
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}
		
		
	}

	
}
