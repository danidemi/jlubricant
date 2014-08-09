package com.danidemi.jlubricant.slf4j.utils;

import com.danidemi.jlubricant.slf4j.Level;

public class Default implements MessageToLogLevelPolicy {

	private Level defaultLevel;
	
	public Default(Level defaultLevel) {
		super();
		this.defaultLevel = defaultLevel;
	}

	@Override
	public Level forMessage(String logmessage) {
		return defaultLevel;
	}

}
