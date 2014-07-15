package com.danidemi.jlubricant.logback;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

public class DenyDuplicationsFilterIntegrationTest {
	
	@Test public void shouldUseTheFilter() {
		
		// given
				
		// ...the tested filter
		DenyDuplicationsFilter filter = new DenyDuplicationsFilter();
		filter.setMaxSize(100);
		filter.setSecondsBetweenEvictions(100);
		filter.setItemMaxAgeInSeconds(30);
		
		// ...logback stopped and ready to work
	    LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
	    Logger rootLogger = ctx.getLogger(Logger.ROOT_LOGGER_NAME);
	    rootLogger.detachAndStopAllAppenders();
	    
	    // ...an out stream
	    OutputStream out = new ByteArrayOutputStream();
	    
	    // ...a pattern layout, pay attention to the format, is important for the test
	    PatternLayoutEncoder layout = new PatternLayoutEncoder();
	    layout.setContext(ctx);
	    layout.setPattern("[%level %msg]");
	    layout.setImmediateFlush(true);
	    layout.start();
	    
	    // ...a console appender that logs through the filter
	    OutputStreamAppender<ILoggingEvent> appender = new OutputStreamAppender<ILoggingEvent>();
	    appender.setContext(ctx);
	    appender.setEncoder(layout);
		appender.addFilter( filter );
		appender.setOutputStream(out);
	    appender.start();
	    	 
	    // ...a logger we can use for the test
	    Logger testLogger = ctx.getLogger("test");
	    testLogger.addAppender(appender);
	    testLogger.setLevel(Level.ALL);
	    
	    


	    // when
	    testLogger.trace("Hello World"); // first occurrence, should be logged
	    testLogger.debug("Hello World"); // second occurrence, should be filtered out
	    testLogger.info ("Hello Italy"); // this is new, should be logged
	    testLogger.error("Hello Italy"); // again a repetition
	    testLogger.warn ("Hello World"); // again a repetition
	    testLogger.info ("Hello World"); // again a repetition
	    
	    
	    
	    
	    // then
	    appender.stop();
	    String string = out.toString();
		assertThat( string, equalTo("[TRACE Hello World][INFO Hello Italy]") );
	   
	}
	
}
