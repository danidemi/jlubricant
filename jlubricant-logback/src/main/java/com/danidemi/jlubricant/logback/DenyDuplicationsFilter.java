package com.danidemi.jlubricant.logback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class DenyDuplicationsFilter extends AbstractMatcherFilter<ILoggingEvent> {
	
	private static final Logger log = LoggerFactory.getLogger(DenyDuplicationsFilter.class);

	private final Map<String, Long> message2lasttimestamp = new HashMap<String, Long>();
	private final LinkedList<String> messages = new LinkedList<String>();
	private long threshold;
	private int maxSize;
	private Thread evicting;
	private long maxAgeInMillis;

	private long millisBetweenEvictions;


	public DenyDuplicationsFilter() {
		setThreshold(TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES));
		setMaxSize(50);
		evicting = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					
					try {
						Thread.sleep(millisBetweenEvictions());
					} catch (InterruptedException e) {
						
					}
					
					if(!Thread.currentThread().isInterrupted()){
					try {
						DenyDuplicationsFilter.this.evict();
					} catch (Exception e) {
					}
					}
					
				}


			}

		});
		evicting.setName("CacheEvictor");
		evicting.start();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				evicting.interrupt();
			}
		}));

	}
	
	public void setSecondsBetweenEvictions(int seconds){
		millisBetweenEvictions = TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS);
	}
	
	private long millisBetweenEvictions() {
		return millisBetweenEvictions;
	}

	protected void evict() {
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
	
	private void cachePut(String message, long timestamp) {
		
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

	private void cacheRemove(String toBeRemoved) {
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
	public FilterReply decide(ILoggingEvent e) {

		String message = e.getFormattedMessage();
		long timeStamp = e.getTimeStamp();
		Long lastTimestamp = message2lasttimestamp.get(message);

		FilterReply result;
		if (lastTimestamp != null) {

			// update the item in the cache putting the message at the beginning
			// of the list
			cachePut(message, timeStamp);

			long deltaFromLastOccurence = timeStamp - lastTimestamp;
			result = (deltaFromLastOccurence > threshold) ? FilterReply.NEUTRAL
					: FilterReply.DENY;
		} else {

			// add the item at the beginning of the list, removing any item if
			// the list is too big
			message2lasttimestamp.put(message, timeStamp);
			messages.addFirst(message);
			while (messages.size() > maxSize) {
				String removeLast = messages.removeLast();
				message2lasttimestamp.remove(removeLast);
			}

			result = FilterReply.NEUTRAL;
		}

		return result;

	}



	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

	public int itemsInCache() {
		return message2lasttimestamp.size();
	}

	public void setMaxSize(int i) {
		this.maxSize = i;
	}

	public void setItemMaxAgeInSeconds(int i) {
		maxAgeInMillis = TimeUnit.MILLISECONDS.convert(i, TimeUnit.SECONDS);
	}

}
