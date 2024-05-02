package uk.co.oliford.cache.randomAccessCache;

import java.util.Collection;
import java.util.HashMap;

import uk.co.oliford.cache.common.CacheService;
import uk.co.oliford.jolu.OneLiners;
import uk.co.oliford.jolu.SettingsManager;


/** Part of the Minerva environment general caching service.
 * Caching service based on files on the local disk.
 * 
 * We don't have an index now, we just go by fileName.
 * 
 * Files are [cachePath]/[cacheName]/minervaCache_[cacheName]_[setName]_[tag].mrc
 * 
 *
 * @author oliford codes@oliford.co.uk 
 * Created 1/7/2011
 */ 
public class RACacheService implements CacheService {

	/** General cache mode for all cache types, one of CacheService.CACHEMODE_xxxx 
	 * Leaving this on FULL will delegate to individual caches. */
	private int generalCacheMode = CacheService.CACHEMODE_FULL;
	
	/** The list of caches for each cache type */
	HashMap<String, RACache> caches = new HashMap<>();
	
	@Override
	public Object get(String cacheName, String setKeyName,
			Object requestKey) {
		
		if(generalCacheMode == CacheService.CACHEMODE_OFF ||
				generalCacheMode == CacheService.CACHEMODE_UPDATE)
			return null;

		RACache c = getCache(cacheName);		
		return c.get(setKeyName, requestKey);
	}
	

	@Override
	public void put(String cacheName, String setKeyName,
								Object requestKey, Object object) {
		if(generalCacheMode == CacheService.CACHEMODE_READONLY ||
				generalCacheMode == CacheService.CACHEMODE_OFF)
			return; //nothing to do
		
		RACache c = getCache(cacheName);		
		c.put(setKeyName, requestKey, object);
	}
	
	/** Get the cache object for the given cache (if you're in minerva, don't store it) */
	@Override
	public RACache getCache(String cacheName){
		
		RACache c = caches.get(cacheName);
		if(c == null){
			//make it
			c = new RACache(cacheName, generalCacheMode, null, false);
			caches.put(cacheName, c);
		}
		
		return c;
	}

	/** Returns the path to the cache files from the minerva settings manager */
	public static String getCachePath(){
		return SettingsManager.defaultGlobal().getPathProperty("minerva.cache.path",
					System.getProperty("java.io.tmpdir") + "/minerva/cache");
	}

	@Override
	public void clearMemory() {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteSet(String cacheType, String setKeyName) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setCacheMode(String cacheName, int cacheMode) {
		RACache c = caches.get(cacheName);
		if(c != null)
			c.setCacheMode(cacheMode);
		else{
			//make it, so it gets picked up later
			c = new RACache(cacheName, cacheMode, null, false);
			caches.put(cacheName, c);
		}	
	}
	
	@Override
	public void setCacheModeAll(int cacheMode) {
		for(RACache c : caches.values())			
			c.setCacheMode(cacheMode);
	}
	
	@Override
	public int getCacheMode(String cacheName) {
		RACache c = caches.get(cacheName);
		if(c == null)
			throw new RuntimeException("Trying to get mode for cache type '"+cacheName+"', but we don't have one of them yet.");
		return c.getCacheMode();
	}
	
	public Collection<RACache> getAllCaches() {		 
		return caches.values();
	}

	@Override
	public void setCacheTag(String cacheName, String tagName, boolean copyExisting) {
		RACache c = caches.get(cacheName);
		if(c != null)
			c.setCacheTag(tagName, copyExisting);
		else{
			//make it, so it gets picked up later
			c = new RACache(cacheName, generalCacheMode, tagName, copyExisting);
			caches.put(cacheName, c);
		}
	}

	@Override
	public String getCacheTag(String cacheName) {
		RACache c = caches.get(cacheName);
		if(c == null)
			throw new RuntimeException("Trying to get tag for cache type '"+cacheName+"', but we don't have one of them yet.");
		return c.getCacheTag();
	}
	
	
	@Override
	public void setCacheMode(int cacheMode){ this.generalCacheMode = cacheMode; }
	
	@Override
	public int getCacheMode(){ return generalCacheMode; }

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public void fastSync() {
		// TODO Auto-generated method stub
		// ask the files we have in memory to do a sync
	}

	/** Just until we fix the SDK dependancy problem and/or 
	 * rewrite ServiceManager */
	private static RACacheService instance = null;
	public static CacheService instance() {
		if(instance == null)
			instance = new RACacheService();
		return instance;
	}

	/** Turns an arbitrary string into one that will work for
	 * filesystems, and doesn't include the '_' that we use to separate
	 * them in the filename.
	 * 
	 * @param name
	 * @return
	 */
	public static String forFilename(String name) {
		return OneLiners.sanitizeFilename(name).replaceAll("_", "%" + Integer.toHexString('_'));
		
	}
}
