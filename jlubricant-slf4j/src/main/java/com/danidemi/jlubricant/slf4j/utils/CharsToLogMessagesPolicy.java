package com.danidemi.jlubricant.slf4j.utils;

/** Specify how a string should divided in log messages. */
public interface CharsToLogMessagesPolicy {

	public interface Callback {
		void log(String log);
	}

	void onWrite(char[] copyOfRange);

	void setCallback(CharsToLogMessagesPolicy.Callback lubricantLoggerWriter);

	void onFlush();

	void onClose();

}
