package com.danidemi.jlubricant.logback;

import static ch.qos.logback.core.spi.FilterReply.DENY;
import static ch.qos.logback.core.spi.FilterReply.NEUTRAL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
public class DenyDuplicationsFilterTest {
	
	@BeforeClass public static void setUpLogging() {
		ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.setLevel(Level.ALL);
	}
	
	@Test public void shouldNotEvictTooYoungItems() throws InterruptedException {
		
		// given
		DenyDuplicationsFilter myFilter = new DenyDuplicationsFilter();
		myFilter.setSecondsBetweenEvictions(0);
		myFilter.setItemMaxAgeInSeconds(100);
		myFilter.setMaxSize(100);
		
		for(int i = 0; i<100; i++){
			myFilter.decide( loggingEventWithMessageAndTimestamp("msg {}", System.currentTimeMillis(), i) );			
		}				
		
		Thread.sleep(1000);
		
		assertThat( myFilter.itemsInCache(), equalTo(100) );
		
	}
	
	@Test public void shouldEvictEveryNowAndThen() throws InterruptedException {
		
		// given
		DenyDuplicationsFilter myFilter = new DenyDuplicationsFilter();
		myFilter.setSecondsBetweenEvictions(0);
		myFilter.setItemMaxAgeInSeconds(0);
		
		for(int i = 0; i<100; i++){
			myFilter.decide( loggingEventWithMessageAndTimestamp("msg {}", i, 100 * i) );			
		}				
		
		Thread.sleep(2000);
		
		assertThat( myFilter.itemsInCache(), equalTo(0) );
		
	}
	
	@Test public void shouldSetInitialSizeTo50() {
		
		// given
		// ...a filter without an explicit max cache dimension set
		DenyDuplicationsFilter myFilter = new DenyDuplicationsFilter();
		
		// when
		// ..lot of items are decided 
		for(int i = 0; i<100; i++){
			myFilter.decide( loggingEventWithMessageAndTimestamp("msg {}", i, 100 * i) );			
		}		
		
		// then
		// ...just 50 items are in the cache
		assertThat( myFilter.itemsInCache(), is(50) );
				
	}
	
	@Test public void differentMessageShouldNotBeFilteredOut() {
		
		// given
		DenyDuplicationsFilter f = new DenyDuplicationsFilter();
		
		// then
		assertThat( f.decide( loggingEventWithMessageAndTimestamp("Hello {}", 1000, "John") ), is(NEUTRAL) );
		assertThat( f.decide( loggingEventWithMessageAndTimestamp("Hello {}", 1000, "Doris") ), is(NEUTRAL) );
		
	}
	
	@Test public void countItems() {
		
		// given
		DenyDuplicationsFilter f = new DenyDuplicationsFilter();
		
		// then
		assertThat( f.itemsInCache(), is(0) );
		
		// when
		f.decide(loggingEventWithMessageAndTimestamp("1", 100));
		
		// then
		assertThat( f.itemsInCache(), is(1) );
		
		// when
		f.decide(loggingEventWithMessageAndTimestamp("2", 100));
		
		// then
		assertThat( f.itemsInCache(), is(2) );		
		
		// when
		f.decide(loggingEventWithMessageAndTimestamp("3", 100));
		
		// then
		assertThat( f.itemsInCache(), is(3) );		
		
		// when
		f.decide(loggingEventWithMessageAndTimestamp("4", 100));
		
		// then
		assertThat( f.itemsInCache(), is(4) );		
		
	}
	
	@Test public void shouldKeepTheCacheSizeUnderTheMaxSize() {
		
		// given
		DenyDuplicationsFilter f = new DenyDuplicationsFilter();
		f.setMaxSize(5);
		
		// then
		// ...outcome for messages as "Hello 1", "Hello 2" up to "Hello 10" 
		// should be NEUTRAL because they are all seen for the first time.
		for(int i = 0; i<10; i++){
			assertThat( f.decide( loggingEventWithMessageAndTimestamp("Hello {}", i, i+1) ), is(NEUTRAL) );			
		}
		
		// then 
		// ...another "Hello 10" is decided. This should be denied, because is the same than last event
		FilterReply decide = f.decide( loggingEventWithMessageAndTimestamp("Hello {}", 11, 10) );
		assertThat( decide, is(DENY) );
		
		// then 
		// ...another "Hello 1" is decided. This should not be refused, because it is supposed to having being evicted by the cache.		
		assertThat( f.decide( loggingEventWithMessageAndTimestamp("Hello 1", 11, 10) ), is(NEUTRAL) );
		
		assertThat( f.itemsInCache(), is(5) );
		
	}
	
	@Test public void justToBeSureAboutHowGetMessageAndGetFormattedMessageWork() {
		
		// when
		LoggingEvent loggingEvent = new LoggingEvent();
		loggingEvent.setMessage("Hello {}");
		loggingEvent.setArgumentArray(new Object[]{"John"});
		
		// then
		assertEquals( "Hello John", loggingEvent.getFormattedMessage() );
		assertEquals( "Hello {}", loggingEvent.getMessage() );
		
	}
	
	@Test public void shouldSupportConfigurableThreshold() {
		
		// given
		ILoggingEvent event1 = loggingEventWithMessageAndTimestamp("The msg", 4321L);
		ILoggingEvent event2 = loggingEventWithMessageAndTimestamp("The msg", 4321L + 10 + 1);
		
		// when
		// ...the threshold is smaller than the difference between two equal
		// messages, they should be allowed.
		DenyDuplicationsFilter filter = new DenyDuplicationsFilter();
		filter.setItemMaxAgeInMillis(10);
		FilterReply outcome1 = filter.decide(event1);
		FilterReply outcome2 = filter.decide(event2);
		
		// then
		assertThat(outcome1, equalTo( FilterReply.NEUTRAL ));
		assertThat(outcome2, equalTo( FilterReply.NEUTRAL ));
		
		// when
		// ...the threshold is bigger than the difference between two equal
		// messages, they should be allowed.		
		filter = new DenyDuplicationsFilter();
		filter.setItemMaxAgeInMillis(11);
		outcome1 = filter.decide(event1);
		outcome2 = filter.decide(event2);
		
		// then
		assertThat(outcome1, equalTo( FilterReply.NEUTRAL ));
		assertThat(outcome2, equalTo( FilterReply.DENY ));
			
	}
	
	@Test public void shouldAllowSecondLoggingEventInTheFarFutureWithSameMessage() {
		
		// given
		DenyDuplicationsFilter f = new DenyDuplicationsFilter();
		ILoggingEvent event1 = loggingEventWithMessageAndTimestamp("System started", 1000L);
		ILoggingEvent event2 = loggingEventWithMessageAndTimestamp("System started", 20000000L);
		
		// when
		FilterReply outcome1 = f.decide(event1);
		FilterReply outcome2 = f.decide(event2);
		
		assertThat(outcome1, equalTo( FilterReply.NEUTRAL ));
		assertThat(outcome2, equalTo( FilterReply.NEUTRAL ));		
		
	}
	
	@Test public void shouldDenyCloseSecondLoggingEventWithSameMessage() {
		
		// given
		DenyDuplicationsFilter f = new DenyDuplicationsFilter();
		ILoggingEvent event1 = loggingEventWithMessageAndTimestamp("Hola", 0L);
		ILoggingEvent event2 = loggingEventWithMessageAndTimestamp("Hola", 1000 * 60 * 30);
				
		// when
		FilterReply outcome1 = f.decide(event1);
		FilterReply outcome2 = f.decide(event2);
		
		// then
		assertThat(outcome1, equalTo( FilterReply.NEUTRAL ));
		assertThat("This should be denied because when the same message is sent again after 1000 ms, it should be filtered", outcome2, equalTo( FilterReply.DENY ));
				
	}

	private ILoggingEvent loggingEventWithMessageAndTimestamp(String msg,
			long timestamp, Object... params) {
		
		LoggingEvent event2 = new LoggingEvent();
		event2.setTimeStamp(timestamp);
		event2.setMessage(msg);
		if(params!=null){
			event2.setArgumentArray(params);
		}
		
		return event2;
	}

}
