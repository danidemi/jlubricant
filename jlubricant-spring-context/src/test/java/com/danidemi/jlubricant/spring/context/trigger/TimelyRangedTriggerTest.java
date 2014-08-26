package com.danidemi.jlubricant.spring.context.trigger;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

@RunWith(PowerMockRunner.class)
@PrepareForTest()
public class TimelyRangedTriggerTest {
	
	@Rule public Timeout timeout = new Timeout(3000); 
	
	@Mock TriggerContext context;
	@Mock Trigger delegate;
	
	@Test public void shouldWorkWithInverseLogic(){
		
		// given	
		when(delegate.nextExecutionTime(any(TriggerContext.class)))
			.thenReturn(dateAs(2000, Calendar.JANUARY, 5, 22, 0, 0))
			.thenReturn(dateAs(2000, Calendar.JANUARY, 6, 3, 0, 0))
			.thenReturn(dateAs(2000, Calendar.JANUARY, 7, 0, 30, 0)) // <--- this is the first date that is ok!
			.thenReturn(dateAs(2000, Calendar.JANUARY, 8, 1, 30, 0));
		
		// a trigger configured to trigger from 23:00 to 2:00
		TimelyRangedTrigger tested = new TimelyRangedTrigger(); 
		tested.setStart("23:00:00");
		tested.setEnd("2:00:00");
		tested.setDelegate(delegate);
		
		// when
		Date nextExecutionTime = tested.nextExecutionTime(context);
		
		// then
		assertThat( nextExecutionTime, equalTo(dateAs(2000, Calendar.JANUARY, 7, 0, 30, 0)) );
		
	}
	
	@Test public void shouldRaiseExceptionIfUnderlyingTriggerWontTriggerInTheComingFuture() {
		
		// given	
		CronTrigger t = new CronTrigger("0 35 4 * * *"); //always triggers at 4:35
		
		// ...the TimelyRangedTrigger is configured in a way that will never trigger the underlying
		TimelyRangedTrigger tested = new TimelyRangedTrigger();
		tested.setStart("8:00:00");
		tested.setEnd("9:00:00");
		tested.setDelegate(t);			
		
		// when
		try{
			tested.nextExecutionTime(context);
			fail();
		}catch(UndefinedTrigger ut){
			
		}
		
	}
	
	@Test public void shouldStopSearchWhenUnderlyingTriggerWontRunAnyMore(){
		
		// given	
		when(delegate.nextExecutionTime(any(TriggerContext.class)))
			.thenReturn(null);
		
		// when
		TimelyRangedTrigger tested = new TimelyRangedTrigger();
		tested.setStart("8:00:00");
		tested.setEnd("9:00:00");
		tested.setDelegate(delegate);	
		
		// when
		Date nextExecutionTime = tested.nextExecutionTime(context);
		
		// then
		assertThat( nextExecutionTime, nullValue() );		
		
		
	}

	@Test public void shouldScheduleTriggerInTheMorning() {
		
		// given	
		when(delegate.nextExecutionTime(any(TriggerContext.class)))
			.thenReturn(dateAs(2000, Calendar.JANUARY, 5, 14, 0, 0))
			.thenReturn(dateAs(2000, Calendar.JANUARY, 6, 15, 0, 0))
			.thenReturn(dateAs(2000, Calendar.JANUARY, 7, 8, 30, 0)) // <--- this is the first date that is ok!
			.thenReturn(dateAs(2000, Calendar.JANUARY, 8, 8, 30, 0));
		
		TimelyRangedTrigger tested = new TimelyRangedTrigger();
		tested.setStart("8:00:00");
		tested.setEnd("9:00:00");
		tested.setDelegate(delegate);
		
		// when
		Date nextExecutionTime = tested.nextExecutionTime(context);
		
		// then
		assertThat( nextExecutionTime.getTime(), equalTo(dateAs(2000, Calendar.JANUARY, 7, 8, 30, 0).getTime()) );
		
	}
	
	@Test public void shouldScheduleTriggerInTheAfternoon() {
		
		// given	
		when(delegate.nextExecutionTime(any(TriggerContext.class)))
			.thenReturn(dateAs(2000, Calendar.JANUARY, 5, 14, 0, 0))
			.thenReturn(dateAs(2000, Calendar.JANUARY, 6, 15, 0, 0)) // <--- this is the first date that is ok!
			.thenReturn(dateAs(2000, Calendar.JANUARY, 7, 8, 30, 0)) 
			.thenReturn(dateAs(2000, Calendar.JANUARY, 8, 8, 30, 0));
		
		TimelyRangedTrigger tested = new TimelyRangedTrigger();
		tested.setStart("14:30:00");
		tested.setEnd("15:30:00");
		tested.setDelegate(delegate);
		
		// when
		Date nextExecutionTime = tested.nextExecutionTime(context);
		
		// then
		assertThat( nextExecutionTime, equalTo(dateAs(2000, Calendar.JANUARY, 6, 15, 0, 0)) );
		
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
	
}
