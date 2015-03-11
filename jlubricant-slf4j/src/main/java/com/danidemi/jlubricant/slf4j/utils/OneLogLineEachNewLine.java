package com.danidemi.jlubricant.slf4j.utils;


import java.util.StringTokenizer;

public class OneLogLineEachNewLine implements CharsToLogMessagesPolicy {

    private static final String LINE_SEPARATOR = System.lineSeparator();

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

        String s = new String(copyOfRange);

        int start = 0;
        int end = 0;

        while(start<s.length()){
            end = s.indexOf(LINE_SEPARATOR, start);
            if(end >= 0){
                String substring = s.substring(start, end);
                sb.append(substring);
                sendCurrentBufferToLog();
                start = end + LINE_SEPARATOR.length();
            }else{
                sb.append( s.substring( start ) );
                start = s.length();
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
