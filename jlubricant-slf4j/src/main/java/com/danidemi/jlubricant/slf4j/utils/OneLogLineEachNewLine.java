package com.danidemi.jlubricant.slf4j.utils;


public class OneLogLineEachNewLine implements CharsToLogMessagesPolicy {

	private StringBuilder sb;
	private Callback cb;
	
	public OneLogLineEachNewLine() {
		sb = new StringBuilder();
	}

	public OneLogLineEachNewLine(Callback callback) {
		this.cb = callback;
	}

	@Override
	public void onWrite(char[] copyOfRange) {
		if(sb == null) sb = new StringBuilder();
		
		for (int i = 0; i < copyOfRange.length; i++) {
			char c = copyOfRange[i];
			if(c != '\n'){
				sb.append(c);				
			}else{
				sendCurrentBufferToLog();
			}
		}
		
	}

	private void sendCurrentBufferToLog() {
		String toLog = sb.toString();
		cb.log(toLog);
		sb=new StringBuilder();
	}

	@Override
	public void setCallback(Callback lubricantLoggerWriter) {
		this.cb = lubricantLoggerWriter;
	}

	@Override
	public void onFlush() {
		sb = new StringBuilder();
	}

	@Override
	public void onClose() {
		sendCurrentBufferToLog();
	}

	
}
