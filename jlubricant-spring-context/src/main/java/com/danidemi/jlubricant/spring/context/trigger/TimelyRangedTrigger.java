package com.danidemi.jlubricant.spring.context.trigger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Range;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

/**
 * This is a {@link Trigger} that activate an underlying trigger in given time intervals.
 * 
 * For instance, you can have a trigger that triggers at fixed delay on one minutes. 
 * This means the scheduled task will run the next time after one minute elapsed from the end of the current run.
 * But for any reason, you want this trigger to be active from 8:00 to 17:00.
 * This timely trigger allows you to do that.
 * 
 * The way in which this trigger apply the "filter" is to fake executions in order to "cheat" the underlying trigger.
 * The {@link TimelyRangedTrigger} will keep on moving forward the executions until when it will receive an execution that 
 * satisfy the filter. Currently the {@link TimelyRangedTrigger} search in the future for one year.
 */
public class TimelyRangedTrigger implements Trigger {

	private static final String ISO_TIME_FORMAT_STRING = "HH:mm:ss";
	private static final SimpleDateFormat NUMERIC_TIME_FORMAT = new SimpleDateFormat("HHmmss.S");
	private static final SimpleDateFormat ISO_TIME_FORMAT = new SimpleDateFormat(ISO_TIME_FORMAT_STRING);
	private Trigger delegate;
	private Double start;
	private Double end;
		
	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		
		if(start == null){
			throw new IllegalArgumentException("Please, set a start time");
		}
		if(end == null){
			throw new IllegalArgumentException("Please, set end time");
		}		
		if(start == end){
			throw new IllegalArgumentException("Start and end time cannot be ==");
		}
		
		ContainsTest containsTest = containmentTest(start, end);
		
		OverriddenContext fakeTriggerContext = new OverriddenContext( triggerContext );
		
		while(true){
			
			Date nextDateFromDelegate = delegate.nextExecutionTime(fakeTriggerContext);
			if(nextDateFromDelegate == null) return null;
			
			Double nextTriggerFromDelegate = new Double( NUMERIC_TIME_FORMAT.format(nextDateFromDelegate) );
			
			if(containsTest.contains(nextTriggerFromDelegate)){
				return nextDateFromDelegate;
			}
			
			fakeTriggerContext = fakeTriggerContext.override(nextDateFromDelegate);
			
			if(nextDateFromDelegate.getTime() - System.currentTimeMillis() > TimeUnit.MILLISECONDS.convert(366, TimeUnit.DAYS)){
				throw new UndefinedTrigger("Unable to find another executions in the near future.");
			}
					
		}
	}

	private ContainsTest containmentTest(final double doubleStarts, final double doubleEnds) {
		ContainsTest containsTest = null;
		if(start<end){
			
			containsTest = new ContainsTest(){
				
				final Range<Double> range = Range.between(doubleStarts, doubleEnds);
				@Override
				public boolean contains(Double dateAsDouble) {
					return range.contains(dateAsDouble);
				}
				
			};
		}else{
			containsTest = new ContainsTest(){
				
				final Range<Double> range = Range.between(doubleStarts, doubleEnds);
				@Override
				public boolean contains(Double dateAsDouble) {
					return !range.contains(dateAsDouble);
				}
				
			};			
		}
		return containsTest;
	}

	public void setStart(String isoTimeStart) {
		try {
			this.start = new Double( NUMERIC_TIME_FORMAT.format(ISO_TIME_FORMAT.parse(isoTimeStart)) );
		} catch (NumberFormatException | ParseException e) {
			throw new IllegalArgumentException("'" + isoTimeStart + "' does not represent a time in ISO format " + ISO_TIME_FORMAT_STRING + ".");
		}
	}

	public void setEnd(String isoTimeEnd) {
		try {
			this.end = new Double( NUMERIC_TIME_FORMAT.format(ISO_TIME_FORMAT.parse(isoTimeEnd)) );
		} catch (NumberFormatException | ParseException e) {
			throw new IllegalArgumentException("'" + isoTimeEnd + "' does not represent a time in ISO format " + ISO_TIME_FORMAT_STRING + ".");
		}		
	}

	public void setDelegate(Trigger trigger) {
		this.delegate = trigger;
	}
	
	private Date dateAs(int year, int month, int date, int hourOfDay,
			int minute, int second) {
		TimeZone tz = TimeZone.getDefault();
		Date theDate = dateAs(tz, year, month, date, hourOfDay, minute, second);
		return theDate;
	}

	private Date dateAs(TimeZone tz, int year, int month, int date,
			int hourOfDay, int minute, int second) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(tz);
		gregorianCalendar.clear();
		gregorianCalendar.set(year, month, date, hourOfDay, minute, second);
		Date time = gregorianCalendar.getTime();
		return time;
	}
	
	private static interface ContainsTest {
		public boolean contains(Double d);
	}
		
	/** An immutable {@link TriggerContext} that quickly allow to obtain copies in which the execution date is overridden. */
	private static class OverriddenContext implements TriggerContext {

		private TriggerContext context;
		private Date overridden;

		public OverriddenContext(TriggerContext context) {
			super();
			this.context = context;
			this.overridden = null;
		}
		
		public OverriddenContext(TriggerContext context, Date overridden) {
			super();
			this.context = context;
			this.overridden = overridden;
		}

		/**
		 * Return a clone of this context in which {@code lastScheduledExecutionTime} and 
		 * {@code lastCompletionTime} are overridden with the provided value.
		 */
		public OverriddenContext override(Date override){
			return new OverriddenContext(context, override); 
		}

		public Date lastScheduledExecutionTime() {
			return overridden==null ? context.lastScheduledExecutionTime() : overridden;
		}

		public Date lastActualExecutionTime() {
			return context.lastActualExecutionTime();
		}

		public Date lastCompletionTime() {
			return overridden==null ? context.lastCompletionTime() : overridden;
		}
				
	}	

}
