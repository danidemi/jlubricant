package com.danidemi.jlubricant.slf4j.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public abstract class AbstractLoggerWriter extends Writer {

	public AbstractLoggerWriter(CharsToLogMessagesPolicy oneLogLineEachNewLine) {
		this.policy = oneLogLineEachNewLine;
	}

	protected final CharsToLogMessagesPolicy policy;

	protected abstract void log(String logmessage);

	@Override
	public final void write(char[] cbuf, int off, int len)
			throws IOException {
				this.policy.onWrite(Arrays.copyOfRange(cbuf, off, off + len));
			}

	@Override
	public final void flush() throws IOException {
		this.policy.onFlush();		
	}

	@Override
	public final void close() throws IOException {
		this.policy.onClose();
	}

}
