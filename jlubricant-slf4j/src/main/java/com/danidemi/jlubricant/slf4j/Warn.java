package com.danidemi.jlubricant.slf4j;

import org.slf4j.Marker;

public class Warn extends Level {
	
	@Override
	void log(org.slf4j.Logger logger, Marker arg0, String arg1, Object arg2, Object arg3) {
		logger.warn(arg0, arg1, arg2, arg3);
	}

	@Override
	void log(org.slf4j.Logger logger, Marker arg0, String arg1, Object... arg2) {
		logger.warn(arg0, arg1, arg2);
	}

	@Override
	void log(org.slf4j.Logger logger, Marker arg0, String arg1, Object arg2) {
		logger.warn(arg0, arg1, arg2);
	}

	@Override
	void log(org.slf4j.Logger logger, Marker arg0, String arg1, Throwable arg2) {
		logger.warn(arg0, arg1, arg2);
	}

	@Override
	void log(org.slf4j.Logger logger, Marker arg0, String arg1) {
		logger.warn(arg0, arg1);
	}

	@Override
	void log(org.slf4j.Logger logger, String arg0, Object arg1, Object arg2) {
		logger.warn(arg0, arg1, arg2);
	}

	@Override
	void log(org.slf4j.Logger logger, String arg0, Object... arg1) {
		logger.warn(arg0, arg1);
	}

	@Override
	void log(org.slf4j.Logger logger, String arg0, Object arg1) {
		logger.warn(arg0, arg1);
	}

	@Override
	void log(org.slf4j.Logger logger, String arg0, Throwable arg1) {
		logger.warn(arg0, arg1);
	}

	@Override
	void log(org.slf4j.Logger logger, String arg0) {
		logger.warn(arg0);
	}

}
