package com.danidemi.jlubricant.logback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryCache implements Cache, Serializable {
	
	private static final long serialVersionUID = -4591895608685571984L;

	private static final Logger log = LoggerFactory.getLogger(MemoryCache.class);
	
	private final Map<String, Long> message2lasttimestamp = new HashMap<String, Long>();
	private final LinkedList<String> messages = new LinkedList<String>();
	private int maxSize;
	
	
	@Override
	public synchronized void cacheEvict(long maxAgeInMillis) {
		long now = System.currentTimeMillis();
		ArrayList<String> toBeRemoved = new ArrayList<String>();
		for (Map.Entry<String, Long> e : this.message2lasttimestamp.entrySet()) {
			if( now - e.getValue() > maxAgeInMillis ){
				toBeRemoved.add( e.getKey() );
			}
		}

		if(!toBeRemoved.isEmpty()){
			long preSize = cacheSize();
			for (String string : toBeRemoved) {
				cacheRemove(string);
			}
			long postSize = cacheSize();
			log.trace("{} items evicted from cache, size decreased from {} to {}", toBeRemoved.size(), preSize, postSize);
		}
	}
	
	@Override
	public Long timestampOfLastOccurence(String message) {
		return message2lasttimestamp.get(message);
	}


	@Override
	public synchronized void put(String message, long timestamp) {
		
		Long previous = message2lasttimestamp.put(message, timestamp);
		if(previous!=null){
			boolean contained = messages.remove( message );
			messages.addFirst(message);
		}else{
			messages.addFirst(message);
			while (messages.size() > maxSize) {
				String removeLast = messages.removeLast();
				message2lasttimestamp.remove(removeLast);
			}			
		}
		
		
	}
	
	private synchronized void cacheRemove(String toBeRemoved) {
		Long remove = message2lasttimestamp.remove(toBeRemoved);
		boolean removed = messages.remove(toBeRemoved);
		if(!( (removed && remove!=null) || (!removed && remove==null) )){
			throw new RuntimeException("Cache is out of sync");
		}
	}
	
	private long cacheSize() {
		int size = message2lasttimestamp.size();
		if(size - messages.size() != 0){
			throw new RuntimeException("Cache is out of sync");
		}
		return size;
	}	
	
	
	@Override
	public int itemsInCache() {
		return message2lasttimestamp.size();
	}	
	
	@Override
	public synchronized void clear() {
		this.message2lasttimestamp.clear();
		this.messages.clear();
	}
	
	/** 
	 * The max number of previous logging events to be contained in memory.
	 * Bigger values means the filter will more likely deny duplicated log messages in scenarios
	 * where lot of different log messages are issued. However, it will make the filter to consume more memory.
	 */
	@Override
	public void setMaxSize(int i) {
		this.maxSize = i;
	}	
	
}
