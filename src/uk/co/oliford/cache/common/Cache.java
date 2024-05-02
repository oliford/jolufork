package uk.co.oliford.cache.common;

import java.util.List;

/** Interface for a single cache, pulled from the caching service.
 * 
 * DO NOT store these objects in minerva!
 * 
 *  */
public interface Cache {
	
	/**
	 * Retrieves an object from the cache.
	 *
	 * If multiple matching sets are found with different tags, a tag has been set,
	 * and one matches that tag, it will be used quietly. If multiple sets are found
	 * with different tags and no tag is set or the set tag isn't found, the first
	 * is picked and a warning given.
	 * 
	 * This is equivalent to calling CacheService.get(cacheName, setKeyName,
	 * requestKey)
	 * 
	 * @param cacheName  Name of cache to load from. i.e the cache type 'magnetics',
	 *                   'xbase' etc
	 * @param setKeyName Unique name for the key of the primary set, typically built
	 *                   from a friendly name and a unique ID.
	 * @param requestKey The requested key object.
	 * @return The cached object.
	 */
	public Object get(String setKeyName, Object requestKey);
	
	/**
	 * Puts an object into the cache.
	 * 
	 * This is equivalent to calling CacheService.put(cacheName, setKeyName,
	 * requestKey)
	 * 
	 * @param cacheName  Name of cache to write to. i.e the cache type 'magnetics',
	 *                   'xbase' etc
	 * @param setKeyName Unique name for the key of the primary set, typically built
	 *                   from a friendly name and a unique ID.
	 * @param requestKey The key object that will be requested.
	 * @param object     The object to be cached.
	 */
	public void put(String setKeyName, Object requestKey, Object object);
	
	
	/** Returns all the request keys in the given cache set */
	public List<Object> getKeys(String setKeyName);
	
	/** Obviously named setters/getters */
	public int getCacheMode();
	public void setCacheMode(int cacheMode);
	public String getCacheTag();
	public boolean getCopyExisting();
	public void setCacheTag(String tagName, boolean copyExisting);
	
	/** cleanup, defragment, sync etc a given set in the cache. Can be very slow. */
	public void organise(String setKeyName);

}
