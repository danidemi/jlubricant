package com.danidemi.jlubricant.logback;

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


	private Cache cache = new MemoryCache();
	private int maxSize;
	private Thread evicting;
	private long maxAgeInMillis;


	private long millisBetweenEvictions;

	public DenyDuplicationsFilter() {
		
		setItemMaxAgeInSeconds( TimeUnit.SECONDS.convert(30, TimeUnit.MINUTES) );
		cache.setMaxSize(50);
		setSecondsBetweenEvictions(30);
		
		evicting = new Thread(new Runnable() {

			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {

					try {
						Thread.sleep(millisBetweenEvictions());
					} catch (InterruptedException e) {

					}
					
					Thread.yield();

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
		
	protected void cacheEvict() {
		cache.cacheEvict(maxAgeInMillis);
	}

	@Override
	public FilterReply decide(ILoggingEvent e) {
	
		String message = e.getFormattedMessage();
		long timestamp = e.getTimeStamp();
		Long lastTimestamp = cache.timestampOfLastOccurence(message);
	
		cache.put(message, timestamp);
		
		FilterReply result;
		if (lastTimestamp != null) {
			long deltaFromLastOccurence = timestamp - lastTimestamp;
			result = (deltaFromLastOccurence > maxAgeInMillis) ? FilterReply.NEUTRAL
					: FilterReply.DENY;
		} else {
			result = FilterReply.NEUTRAL;
		}
	
		return result;
	
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
	 * 0 means the cache is continuously evicted.
	 */
	public void setSecondsBetweenEvictions(int seconds){
		millisBetweenEvictions = TimeUnit.MILLISECONDS.convert(seconds, TimeUnit.SECONDS);
	}
	
	public long millisBetweenEvictions() {
		return millisBetweenEvictions;
	}

	/**
	 * Empty the cache.
	 */
	public synchronized void clear() {
		cache.clear();
	}

	public Integer itemsInCache() {
		return cache.itemsInCache();
	}

	public void setMaxSize(int i) {
		cache.setMaxSize(i);
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

}
