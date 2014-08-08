package com.danidemi.jlubricant.slf4j;

import org.slf4j.Logger;
import org.slf4j.Marker;

public abstract class Level {

	abstract void log(Logger logger, Marker arg0, String arg1, Object arg2, Object arg3);

	abstract void log(Logger logger, Marker arg0, String arg1, Object... arg2);

	abstract void log(Logger logger, Marker arg0, String arg1, Object arg2) ;

	abstract void log(Logger logger, Marker arg0, String arg1, Throwable arg2);

	abstract void log(Logger logger, Marker arg0, String arg1) ;

	abstract void log(Logger logger, String arg0, Object arg1, Object arg2) ;

	abstract void log(Logger logger, String arg0, Object... arg2);

	abstract void log(Logger logger, String arg0, Object arg1);

	abstract void log(Logger logger, String arg0, Throwable arg1);

	abstract void log(Logger logger, String arg0);

}
