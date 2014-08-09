package com.danidemi.jlubricant.slf4j.utils;

import com.danidemi.jlubricant.slf4j.Level;

/** Defines a policy to decide wich log level has to be assigned to a message. */
public interface MessageToLogLevelPolicy {

	/**
	 * Return the log level that should be used to log this message or null if it was not able to assign a level. 
	 */
	Level forMessage(String logmessage);

}
