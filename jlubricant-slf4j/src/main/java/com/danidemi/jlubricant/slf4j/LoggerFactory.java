package com.danidemi.jlubricant.slf4j;

/**
 * {@link LoggerFactory} that mimic SLF4J's Logger Factory, but return instances of {@link LubricantLogger} instead of SLF4J's loggers.
 */
public class LoggerFactory  {

	private LoggerFactory() {
		throw new UnsupportedOperationException("Not meant to be instantiated");
	}
	
	public static LubricantLogger getLogger(String clazz) {
		return new LubricantLogger( org.slf4j.LoggerFactory.getLogger(clazz) );
	}
	
	public static LubricantLogger getLogger(Class clazz) {
		return new LubricantLogger( org.slf4j.LoggerFactory.getLogger(clazz) );
	}
	
	public static LubricantLogger getLogger(org.slf4j.Logger logger) {
		return new LubricantLogger( logger );
	}		
	
}
