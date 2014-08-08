package com.danidemi.jlubricant.slf4j;

public interface Logger extends org.slf4j.Logger {
	
	public static final Level ERROR = new Error();
	public static final Level WARN = new Warn();
	public static final Level INFO = new Info();
	public static final Level DEBUG = new Debug();
	public static final Level TRACE = new Trace();
	
}
