package com.danidemi.jlubricant.logback;

public interface Cache {

	void cacheEvict(long maxAgeInMillis);

	void put(String message, long timestamp);

	Long timestampOfLastOccurence(String message);

	/** Number of log events in the cache. */
	int itemsInCache();

	/**
	 * Empty the cache.
	 */
	void clear();

	void setMaxSize(int maxSize);

}
