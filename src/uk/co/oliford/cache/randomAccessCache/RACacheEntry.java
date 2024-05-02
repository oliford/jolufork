package uk.co.oliford.cache.randomAccessCache;

import java.lang.ref.SoftReference;

/** A entry into the memory cache HashMap inside RandomAccessCacheSet */
public class RACacheEntry {
	
	protected RACacheEntry(){ }
	
	/** The actual object data, if we have already loaded it, maybe */
	SoftReference<Object> objectRef;
	
	/** The real request key object, if we loaded it */
	SoftReference<Object> keyRef;
	
	/** position in bytes into the file of this entry (if the file hasn't been reorganized)*/
	public long entryHeaderPos;
	
	/** Size, in bytes on disk, of the key serialized package */
	public int keyPackageSize;
	
	/** Size, in bytes on disk, of the object serialized package */ 
	public int objectPackageSize; 
	
	/** For chaining entries with colliding hashes */
	public RACacheEntry nextEntry;
	
}

