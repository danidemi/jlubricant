package com.danidemi.jlubricant.logback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * This filter denies the same log message to be repeatedly written.
 * When it has to decide about an {@link ILoggingEvent} A, it checks whether it has already 
 * allowed another {@link ILoggingEvent} B with the same text in the close past. If that proves
 * to be {@literal true}, the logging event is denied.
 *  
 * Internally the filter keeps track of all log events that it has previously decided about.
 * To avoid this list to grow endlessly, some mechanisms are in place.
 * The number of kept log events is limited. If such limit is reached, older log events are discarded.
 * This means that in case the filter is "flooded" with a lot of log items, each carrying a different log message,
 * it can wrongly allow a log message even if it was duplicated.
 * 
 * The behaviour of the filter can be fine tuned through a set of properties, even if
 * it comes with reasonable defaults.  
 * 
 * @author danidemi
 *
 */
public class DenyDuplicationsFilter extends AbstractMatcherFilter<ILoggingEvent> {
	
	private static final Logger log = LoggerFactory.getLogger(DenyDuplicationsFilter.class);

	private final Map<String, Long> message2lasttimestamp = new HashMap<String, Long>();
	private final LinkedList<String> messages = new LinkedList<String>();
	private int maxSize;
	private Thread evicting;
	private long maxAgeInMillis;

	private long millisBetweenEvictions;

	public DenyDuplicationsFilter() {
		
		setItemMaxAgeInSeconds( TimeUnit.SECONDS.convert(30, TimeUnit.MINUTES) );
		setMaxSize(50);
		setSecondsBetweenEvictions(30);
		
		evicting = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {

					try {
						Thread.sleep(millisBetweenEvictions());
					} catch (InterruptedException e) {

					}

					if (!Thread.currentThread().isInterrupted()) {
						try {
							DenyDuplicationsFilter.this.cacheEvict();
						} catch (Exception e) {
							throw new RuntimeException( "An error occurred while evicting the cache of previously evaluated log items.", e );
						}
					}

				}
				log.info("Thread {} is terminating.");
			}

		});
		evicting.setName( String.format("%s-cache-evictor", getClass().getSimpleName().toLowerCase()) );
		evicting.setDaemon(true);
		log.info("Starting up thread {}.", evicting.getName());
		evicting.start();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				log.info("Shutting down thread {} before termination.", evicting.getName());
				evicting.interrupt();
			}
		}));

	}
		
	@Override
	public FilterReply decide(ILoggingEvent e) {
	
		String message = e.getFormattedMessage();
		long timeStamp = e.getTimeStamp();
		Long lastTimestamp = cacheGet(message);
	
		FilterReply result;
		if (lastTimestamp != null) {
	
			cachePut(message, timeStamp);
	
			long deltaFromLastOccurence = timeStamp - lastTimestamp;
			result = (deltaFromLastOccurence > maxAgeInMillis) ? FilterReply.NEUTRAL
					: FilterReply.DENY;
		} else {
				
			cachePut(message, timeStamp);
	
			result = FilterReply.NEUTRAL;
		}
	
		return result;
	
	}

	private Long cacheGet(String message) {
		return message2lasttimestamp.get(message);
	}

	private synchronized void cacheEvict() {
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
	
	private synchronized void cachePut(String message, long timestamp) {
		
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

	/** Number of log events in the cache. */
	public int itemsInCache() {
		return message2lasttimestamp.size();
	}

	/** 
	 * The max number of previous logging events to be contained in memory.
	 * Bigger values means the filter will more likely deny duplicated log messages in scenarios
	 * where lot of different log messages are issued. However, it will make the filter to consume more memory.
	 */
	public void setMaxSize(int i) {
		this.maxSize = i;
	}
	
	/**
	 * How many seconds should elapse to consider a new log event as not duplicated even if its message
	 * was indeed previously accepted.  
	 */
	public void setItemMaxAgeInSeconds(long seconds) {
		maxAgeInMillis = TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS);
	}
	
	public void setItemMaxAgeInMillis(long millis) {
		maxAgeInMillis = millis;
	}	
	
	/**
	 * Set the interval between two subsequent run of the eviction.
	 */
	public void setSecondsBetweenEvictions(int seconds){
		millisBetweenEvictions = TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS);
	}
	
	public long millisBetweenEvictions() {
		return millisBetweenEvictions;
	}

}
