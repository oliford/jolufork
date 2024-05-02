package uk.co.oliford.cache.common;

/**
 * Main interface of the Minerva environment general caching service
 *
 * @author oliford codes@oliford.co.uk Created 1/7/2011
 */
public interface CacheService {

	/**
	 * Retrieves an object from the cache.
	 *
	 * If multiple matching sets are found with different tags, a tag has been set,
	 * and one matches that tag, it will be used quietly. If multiple sets are found
	 * with different tags and no tag is set or the set tag isn't found, the first
	 * is picked and a warning given.
	 * 
	 * This is equivalent to calling
	 * CacheService.getCache(cacheName).get(setKeyName, requestKey)
	 * 
	 * @param cacheName  Name of cache to load from. i.e the cache type 'magnetics',
	 *                   'xbase' etc
	 * @param setKeyName Unique name for the key of the primary set, typically built
	 *                   from a friendly name and a unique ID.
	 * @param requestKey The requested key object.
	 * @return The cached object.
	 */
	public Object get(String cacheName, String setKeyName, Object requestKey);

	/**
	 * Puts an object into the cache.
	 * 
	 * This is equivalent to calling
	 * CacheService.getCache(cacheName).put(setKeyName, requestKey)
	 * 
	 * @param cacheName  Name of cache to write to. i.e the cache type 'magnetics',
	 *                   'xbase' etc
	 * @param setKeyName Unique name for the key of the primary set, typically built
	 *                   from a friendly name and a unique ID.
	 * @param requestKey The key object that will be requested.
	 * @param object     The object to be cached.
	 */
	public void put(String cacheName, String setKeyName, Object requestKey, Object object);

	/**
	 * Sets a tag name under which to store all future objects passed to
	 * putCachedObject().
	 * 
	 * If copyExisting is true, all future calls to getCachedObject() that match
	 * under other tags, will also be copied to the new tag name. The idea is that
	 * you can use this to separate out all entries required to run a particular
	 * process, without needed to recalculate those things.
	 * 
	 * @param cacheName    The cache to set the tag for.
	 * @param tagName      The new tag name
	 * @param copyExisting If true, all getCachedObject() requests are also copied
	 *                     into this tag.
	 * @return
	 */
	public void setCacheTag(String cacheName, String tagName, boolean copyExisting);
	
	/** @return The current cache tag, see setCacheTag() */
	public String getCacheTag(String cacheName);
	
	
	/** Release anything currently held in memory */ 
	public void clearMemory();
	
	/** Deletes the given primary set from the cache. This actually removes is from 
	 * whatever permanent storage the cache is using, not just from memory. */
	public void deleteSet(String cacheName, String setKeyName);
	
	/** Cache offline, all requests fail and nothing is written. */
	public static final int CACHEMODE_OFF = 1;
	/** Cache read only. Hits will work but new items are not written. */
	public static final int CACHEMODE_READONLY = 2;
	/** Update mode. All requests fail and newly recalculated items will overwrite the old ones. */
	public static final int CACHEMODE_UPDATE = 3;
	/** Full caching. All hits use cached data and calculated items are added */
	public static final int CACHEMODE_FULL = 4;
	
	
	/** Sets the caching mode for a specific cache type to one of CACHEMODE_xxxx */
	public void setCacheMode(String cacheName, int cacheMode);
	
	/** Gets the caching mode for a specific cache type: one of CACHEMODE_xxxx */
	public int getCacheMode(String cacheName);
	
	/** Sets the caching mode for all/new cache types to one of CACHEMODE_xxxx */
	public void setCacheMode(int cacheMode);
	
	/** Gets the caching mode for all/new cache types: one of CACHEMODE_xxxx */
	public int getCacheMode();
	
	public boolean isReadOnly();
	
	/** Request that the cache service makes sure it's sense of what is in
	 * the caches matches the caches on disk. Use this when you
	 * have a little time and want to check the other processes / computers 
	 * haven't been messing with the permenant store.
	 * 
	 * Implementations should try to make this fast (i.e just a check)
	 * and not invalidate anything in memory or reload the whole index 
	 * of anything, but crude implementations might fall back on a complete
	 * reload.  
	 */
	public void fastSync();
	
	/** For convinience, returns an object for working with a given cache.
	 * In Minerva, these are environment level objects 
	 * so DO NOT store them outside of methods. */ 
	public Cache getCache(String cacheName);

	public void setCacheModeAll(int cachemodeReadonly);
	
}
