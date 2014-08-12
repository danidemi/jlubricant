package com.danidemi.jlubricant.slf4j.utils;

import java.io.PrintWriter;
import java.io.Writer;

import org.slf4j.Logger;

import com.danidemi.jlubricant.slf4j.Level;
import com.danidemi.jlubricant.slf4j.LoggerFactory;
import com.danidemi.jlubricant.slf4j.LubricantLogger;

/** A {@link Writer} that writes to a {@link Logger}. */
public class LubricantLoggerWriter extends AbstractLoggerWriter {

	final private LubricantLogger logger;
	final private MessageToLogLevelPolicy guessLevel;

	/** 
	 * Creates a new {@link LubricantLoggerWriter} that writes on the given logger at the given level.
	 */
	public LubricantLoggerWriter(LubricantLogger logger, Level level) {
		this(logger, new OneLogLineEachNewLine(), new Default(level));
	}
	
	/** 
	 * Creates a new {@link LubricantLoggerWriter} that writes on the given logger at the given level.
	 */
	public LubricantLoggerWriter(LubricantLogger logger, MessageToLogLevelPolicy guessLevel) {
		this(logger, new OneLogLineEachNewLine(), guessLevel);
	}	

	/** 
	 * Creates a new {@link LubricantLoggerWriter} that writes on the given logger.
	 * The given {@link CharsToLogMessagesPolicy} has the responsibility to transform the chars provided to this writer into log messages.
	 * The provided {@link MessageToLogLevelPolicy} has the task to decide the level the message will be logged at.
	 */	
	public LubricantLoggerWriter(LubricantLogger logger, CharsToLogMessagesPolicy toLogs,
			MessageToLogLevelPolicy guessLevel) {
		super(toLogs);
		CharsToLogMessagesPolicy.Callback callback = new CharsToLogMessagesPolicy.Callback() {
			
			@Override
			public void log(String log) {
				LubricantLoggerWriter.this.log(log);
			}
		};		
		this.logger = logger;
		toLogs.setCallback(callback);
		this.guessLevel = guessLevel;
	}

	public LubricantLoggerWriter(LubricantLogger logger,
			CharsToLogMessagesPolicy toLogs, Level level) {
		this(logger, toLogs, new Default(level));
	}

	public LubricantLoggerWriter(Logger logger, CharsToLogMessagesPolicy toLogs, Level level) {
		this(LoggerFactory.getLogger( logger ), toLogs, new Default(level));
	}

	@Override
	protected void log(String logmessage) {
		logger.log(guessLevel.forMessage(logmessage), logmessage);		
	}
	
	public final PrintWriter asPrintWriter() {
		return new PrintWriter(this);
	}

}
