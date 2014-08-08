package com.danidemi.jlubricant.slf4j;


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
