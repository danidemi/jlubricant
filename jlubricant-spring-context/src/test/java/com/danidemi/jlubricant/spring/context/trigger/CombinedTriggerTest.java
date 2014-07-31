package com.danidemi.jlubricant.spring.context.trigger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import com.danidemi.jlubricant.spring.context.trigger.CombinedTrigger;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class CombinedTriggerTest {

	@Mock Trigger trigger1;
	@Mock Trigger trigger2;
	@Mock TriggerContext ctx;
	static SimpleDateFormat ISODATE = new SimpleDateFormat("yyyy-mm-dd");
	static Date earlierDate;
	static Date laterDate;

	@BeforeClass
	public static void setUp() throws ParseException {
		earlierDate = ISODATE.parse("2010-06-12");
		laterDate = ISODATE.parse("2012-06-12");
	}

	@Test
	public void shouldReturnTheEarlierDate() throws ParseException {

		// given
		willReturnNextTriggerDate(trigger1, earlierDate);
		willReturnNextTriggerDate(trigger2, laterDate);

		CombinedTrigger tested = new CombinedTrigger();
		tested.add(trigger1);
		tested.add(trigger2);

		// when
		Date nextExecutionTime = tested.nextExecutionTime(ctx);

		// then
		assertThat(nextExecutionTime, equalTo(earlierDate));

	}

	@Test
	public void shouldReturnTheEarlierDateRegardlessOfAddingOrder() throws ParseException {

		// given
		willReturnNextTriggerDate(trigger2, earlierDate);
		willReturnNextTriggerDate(trigger1, laterDate);

		CombinedTrigger tested = new CombinedTrigger();
		tested.add(trigger1);
		tested.add(trigger2);

		// when
		Date nextExecutionTime = tested.nextExecutionTime(ctx);

		// then
		assertThat(nextExecutionTime, equalTo(earlierDate));

	}

	@Test
	public void shouldReturnTheDateReturnedByTheOnlyTrigger() {

		// given
		Date date = new Date();
		willReturnNextTriggerDate(trigger1, date);

		CombinedTrigger tested = new CombinedTrigger();
		tested.add(trigger1);

		// when
		Date nextExecutionTime = tested.nextExecutionTime(ctx);

		// then
		verify(trigger1).nextExecutionTime(ctx);
		assertThat(nextExecutionTime, equalTo(date));


	}

	private void willReturnNextTriggerDate(Trigger trigger12, Date date) {
		when(trigger12.nextExecutionTime(any(TriggerContext.class))).thenReturn(date);
	}

}
