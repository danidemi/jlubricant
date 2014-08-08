package com.danidemi.jlubricant.slf4j.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.slf4j.Logger;

/** A {@link Writer} that writes to a {@link Logger}. */
public class LogWriter extends Writer {

	private Logger logger;
	private StringBuilder sb;

	public LogWriter(Logger logger) {
		this.logger = logger;
		sb = new StringBuilder();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		sb.append(Arrays.copyOfRange(cbuf, off, off + len));
	}

	@Override
	public void flush() throws IOException {
		logger.info(sb.toString().trim().replaceAll("\\n$", ""));
		sb = new StringBuilder();
	}

	@Override
	public void close() throws IOException {
		// nothing really to do.
	}

}
