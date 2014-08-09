package com.danidemi.jlubricant.slf4j.utils;

import java.util.ArrayList;

import com.danidemi.jlubricant.slf4j.Level;

public class Composite implements MessageToLogLevelPolicy {

	private ArrayList<MessageToLogLevelPolicy> logs;
	
	@Override
	public Level forMessage(String logmessage) {
		for (MessageToLogLevelPolicy messageToLogLevelPolicy : logs) {
			Level forMessage = messageToLogLevelPolicy.forMessage(logmessage);
			if(forMessage != null) return forMessage;
		}
		return null;
	}

}
