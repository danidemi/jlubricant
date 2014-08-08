package com.danidemi.jlubricant.slf4j.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

import org.slf4j.Logger;

import com.danidemi.jlubricant.slf4j.Level;
import com.danidemi.jlubricant.slf4j.LubricantLogger;

/** A {@link Writer} that writes to a {@link Logger}. */
public class LubricantLoggerWriter extends Writer {

	final private LubricantLogger logger;
	private StringBuilder sb;
	final private Level level;

	public LubricantLoggerWriter(LubricantLogger logger, Level level) {
		this.logger = logger;
		sb = new StringBuilder();
		this.level = level;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		sb.append(Arrays.copyOfRange(cbuf, off, off + len));
	}

	@Override
	public void flush() throws IOException {
		logger.log(level, sb.toString().trim().replaceAll("\\n$", ""));
		sb = new StringBuilder();
	}

	@Override
	public void close() throws IOException {
		// nothing really to do.
	}

}
