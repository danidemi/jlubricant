package com.danidemi.jlubricant.slf4j.utils;


public class Replace implements CharsToLogMessagesPolicy {

	private CharsToLogMessagesPolicy delegate;
	private Callback clientCallback;
	private String replacement;
	private String regex;
	
	public Replace(CharsToLogMessagesPolicy delegate,
			String regex, String replacement) {
		super();
		this.delegate = delegate;
		this.regex = regex;
		this.replacement = replacement;
	}

	@Override
	public void onWrite(char[] copyOfRange) {
		delegate.onWrite(copyOfRange);
	}

	@Override
	public void setCallback(Callback lubricantLoggerWriter) {
		
		this.clientCallback = lubricantLoggerWriter;
		
		delegate.setCallback( new Callback() {

			@Override
			public void log(String log) {
				clientCallback.log( Replace.this.onLog(log) );
				
			}
		} );

	}

	protected String onLog(String log) {
		return log.replaceAll(regex, replacement);
	}

	@Override
	public void onFlush() {
		delegate.onFlush();
	}

	@Override
	public void onClose() {
		delegate.onClose();
	}

}
