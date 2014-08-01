package com.danidemi.jlubricant.logback;

public class FileSystemCache implements Cache {

	private MemoryCache cache;
	
	public FileSystemCache() {
		this.cache = new MemoryCache();
	}

	public void cacheEvict(long maxAgeInMillis) {
		cache.cacheEvict(maxAgeInMillis);
	}

	public Long timestampOfLastOccurence(String message) {
		return cache.timestampOfLastOccurence(message);
	}

	public void put(String message, long timestamp) {
		cache.put(message, timestamp);
	}

	public int hashCode() {
		return cache.hashCode();
	}

	public int itemsInCache() {
		return cache.itemsInCache();
	}

	public void clear() {
		cache.clear();
	}

	public void setMaxSize(int i) {
		cache.setMaxSize(i);
	}

	public boolean equals(Object obj) {
		return cache.equals(obj);
	}

	public String toString() {
		return cache.toString();
	}
	
	

}
