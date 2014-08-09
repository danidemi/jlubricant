package com.danidemi.jlubricant.slf4j.utils;


public class OneLogLineForEachFlush implements CharsToLogMessagesPolicy {

	private StringBuilder sb;
	private Callback cb;
	
	public OneLogLineForEachFlush() {
		sb = new StringBuilder();
	}

	public OneLogLineForEachFlush(Callback callback) {
		this.cb = callback;
	}

	@Override
	public void onWrite(char[] copyOfRange) {
		if(sb == null) sb = new StringBuilder();
		
		for (int i = 0; i < copyOfRange.length; i++) {
			char c = copyOfRange[i];
			sb.append(c);				
		}
		
	}

	private void sendCurrentBufferToLog() {
		String toLog = sb.toString();
		while(!toLog.isEmpty() && toLog.endsWith("\n")){
			toLog = toLog.substring(0, toLog.length() - 1);
		}
		cb.log(toLog);
		sb=new StringBuilder();
	}

	@Override
	public void setCallback(Callback lubricantLoggerWriter) {
		this.cb = lubricantLoggerWriter;
	}

	@Override
	public void onFlush() {
		sendCurrentBufferToLog();
	}

	@Override
	public void onClose() {
		sendCurrentBufferToLog();
	}

	
}
