package uk.co.oliford.cache.randomAccessCache;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import uk.co.oliford.cache.common.Cache;
import uk.co.oliford.cache.common.CacheService;
import uk.co.oliford.jolu.OneLiners;
import uk.co.oliford.jolu.SettingsManager;


/** One of these for each cache type */
public class RACache implements Cache {
	
	private static final int maxMultipleTagWarnings = Integer.parseInt(SettingsManager.defaultGlobal().getProperty("minerva.cache.maximumMultipleTagWarnings", "10"));
	private boolean suppressMultipleTagWarnings = false;
	private int nMultipleTagWarnings = 0;
	
	/** The identity of us */
	private String cacheName;
	
	/** Cache mode for this cache type, one of CacheService.CACHEMODE_xxxx */
	private int cacheMode;
	
	/** Current tag set for this cache type, or null */
	private String tagName = null;
	
	/** If a tag is set and we find something in another tag but not in the 
	 * active tag set, should we copy it in?? */
	private boolean copyExisting = false;
	
	/** Storage of all cache sets under a given set name.
	 * key is set name
	 * val is list of sets with different tags (you have to check them all!) */
	HashMap<String, RACacheSet[]> sets = new HashMap<>();
	
	/** Only things in this package should instantiate this */
	protected RACache(String cacheName, int cacheMode, String tagName, boolean copyExisting) {
		this.cacheName = cacheName;
		this.cacheMode = cacheMode;
		this.tagName = tagName;
		this.copyExisting = copyExisting;
		
		scanForSets();
	}
	
	/** Look for correctly named '.mrc' files in the correct dir and load their indices */ 
	public void scanForSets(){
		String path = RACacheService.getCachePath() + cacheName; // getCachePath already appends a File.separator
		File dir = new File(path);
		
		if (!dir.isDirectory()) {
			OneLiners.mkdir(path);
			return;
		}
		
		File files[] = dir.listFiles();
		
		for(File file : files){
			String fName = file.getName();
			if(!fName.startsWith("minervaCache_") || ! fName.endsWith(".mrc"))
				continue;
			
			String parts[] = fName.substring(13, fName.length() - 4).split("_");
			
			if(parts.length < 2 || !parts[0].equals(RACacheService.forFilename(cacheName)))
				continue;
			
			String setName = OneLiners.desanitizeFilename(parts[1]);
			String tagName = parts.length < 3 ? null : OneLiners.desanitizeFilename(parts[2]);
			
			try{
				addSetToList(setName, new RACacheSet(cacheName, setName, tagName));
				
			}catch(Exception e){
				System.err.println("RACache: WARNING: Error loading cache entry cache='"+cacheName+"', set='"+setName+"', tag='"+tagName+"'");
			}
		}
	}
	
	@Override
	/** Get an object from this cache */
	public Object get(String setKeyName, Object requestKey) {
		
		if(cacheMode == CacheService.CACHEMODE_OFF ||
				cacheMode == CacheService.CACHEMODE_UPDATE)
			return null;
		
		//now find the list of sets matching
		RACacheSet[] matchingSets = sets.get(setKeyName);
		
		//TODO: We ought to see if the tagged set at least has turned up
		// on disk since we scanned in construction
		if(matchingSets == null) //no matching sets at all, nothing to do
			return null;
		
		
		//if we have an active tag
		if(tagName != null){
			//then first look in that set
			for(RACacheSet set : matchingSets){
				if(tagName.equals(set.getTagName())) {		
					Object val = set.get(requestKey);
					if(val != null)
						return val;	//if we found it, we don't even need to look in the others
					break; //otherwise go search other tags
				}
			}
		}

		//otherwise find it under another tag
		// there could be multiple, but should not be
		RACacheSet foundInSet = null;
		Object foundVal = null;
		for(RACacheSet set : matchingSets){
			Object val = set.get(requestKey);			
			
			if(val != null){ //found one
				
				if(foundInSet != null){ //multiple matches
					System.err.println("RACache: WARNING: Found a cache entry for cache '" + cacheName + 
										"', set '" + setKeyName + 
										"' in a cache set tagged '" + set.getTagName() + 
										"', as well as in one tagged '" + foundInSet.getTagName() + 
										"' but " + (tagName == null ? 
														" there is no active tag." : 
													(" the active tag is '" + tagName + "'")) 
										);

					nMultipleTagWarnings++;
					
					if(nMultipleTagWarnings >= maxMultipleTagWarnings) {
						System.err.println("RACache: Surpressing further multiple tag warnings.");
						suppressMultipleTagWarnings = true;
					}
				}
				
				foundInSet = set;
				foundVal = val;

				//if we arn't giving multitag warnings, we can just use the first we find
				if(suppressMultipleTagWarnings)
					break;	
			}
		}
		
		//if we got here, we found it other a different tag, and may need to copy it 
		//into the new tag set
		if(copyExisting)
			put(setKeyName, requestKey, foundVal);

		return foundVal;
	}
	
	/** @returns An array of RandomAccessCacheSet s that match the given set name */ 
	public RACacheSet[] getAllSets(String setKeyName) {
		//now find the list of sets matching
		return sets.get(setKeyName);
	}

	@Override
	public List<Object> getKeys(String setKeyName) {
		RACacheSet[] allSets = sets.get(setKeyName);

		LinkedList<Object> keys = new LinkedList<>();
		if (allSets != null) {
			for (RACacheSet set : allSets) {
				keys.addAll(set.getKeys());
			}
		}

		return keys;
	}

	@Override
	/** Puts an object into this cache */
	public void put(String setKeyName, Object requestKey, Object object) {
		if(cacheMode == CacheService.CACHEMODE_OFF ||
				cacheMode == CacheService.CACHEMODE_READONLY)
			return;
		
		//now find the list of sets matching sets
		RACacheSet[] matchingSets = sets.get(setKeyName);
		
		//scan the sets
		if (matchingSets != null) {
			for (RACacheSet set : matchingSets) {
				// if the tag matches, or both it and we don't have a tag, then its the right
				// one
				if ((tagName == null && set.getTagName() == null)
						|| (tagName != null && tagName.equals(set.getTagName()))) {

					set.put(requestKey, object); // add it
					return; // and we're done
				}
			}
		}
		
		//now we need to make a new set
		RACacheSet newSet = new RACacheSet(cacheName, setKeyName, tagName);
		
		newSet.put(requestKey, object); //and add it
		
		addSetToList(setKeyName, newSet);
	}

	/** Adds the given RandomAccessCacheSet to the relevant set list */
	private void addSetToList(String setName, RACacheSet newSet){
		RACacheSet[] matchingSets = sets.get(setName);

		if (matchingSets != null) {
			RACacheSet tmp[] = new RACacheSet[matchingSets.length + 1];
			System.arraycopy(matchingSets, 0, tmp, 0, matchingSets.length);
			tmp[matchingSets.length] = newSet;
			matchingSets = tmp;
		} else
			matchingSets = new RACacheSet[] { newSet };

		sets.put(setName, matchingSets);
	}
	
	@Override
	public void organise(String setKeyName) {
		RACacheSet matchingSets[] = sets.get(setKeyName);
		for(RACacheSet set : matchingSets){
			// a full object integrity check is REALLY slow, so has to be explicit
			set.cleanCache(false);
		}
	}
	
	/** Obviously named setters/getters */
	@Override
	public int getCacheMode() { return this.cacheMode; }

	@Override
	public void setCacheMode(int cacheMode) { this.cacheMode = cacheMode; }

	@Override
	public String getCacheTag() { return this.tagName; }

	@Override
	public boolean getCopyExisting() { return this.copyExisting; }

	@Override
	public void setCacheTag(String tagName, boolean copyExisting) { this.tagName = tagName; this.copyExisting = copyExisting;}

	public RACacheSet getTaggedSet(String setKeyName, String tagName) {
		RACacheSet[] sets = getAllSets(setKeyName);

		for (RACacheSet set : sets) {
			if ((tagName == null && set.getTagName() == null) || tagName.equals(set.getTagName()))
				return set;
		}

		return null;
	}
	
	/** Calls RACacheSet.cleanCache() for all sets with the given set name */
	public void cleanAllSets(String setName, boolean checkObjectPackageIntegrity) {
		RACacheSet[] matchingSets = sets.get(setName);
		if (matchingSets != null)
			for (RACacheSet set : matchingSets)
				set.cleanCache(checkObjectPackageIntegrity);
	}

	/**
	 * Remove, from disk and from memory, everything in all sets with the given
	 * setName. The files are left empty but not actually deleted (16 bytes)
	 */
	public void emptyAllSets(String setName) {
		RACacheSet[] matchingSets = sets.get(setName);
		if (matchingSets != null)
			for (RACacheSet set : matchingSets) {
				set.emptyFile();
			}
		sets.put(setName, null);
	}
}
