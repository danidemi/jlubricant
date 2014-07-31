package com.danidemi.jlubricant.spring.context.trigger;

import java.util.Date;

import org.junit.Test;
import org.springframework.scheduling.support.SimpleTriggerContext;

import com.danidemi.jlubricant.spring.context.trigger.OnceTrigger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;


public class OnceTriggerTest {

	@Test
	public void shouldRunOnce() {

		Date testStart = new Date();

		SimpleTriggerContext triggerContext = new SimpleTriggerContext();
		OnceTrigger onceTrigger = new OnceTrigger();

		Date next1 = onceTrigger.nextExecutionTime(triggerContext);

		assertFalse(next1.before(testStart));

		triggerContext.update(next1, next1, next1);

		Date next2 = onceTrigger.nextExecutionTime(triggerContext);

		assertNull(next2);


	}

	@Test
	public void shouldRunOnceAfter() {

		Date testStart = new Date();

		SimpleTriggerContext triggerContext = new SimpleTriggerContext();
		OnceTrigger onceTrigger = new OnceTrigger();
		onceTrigger.setAfter(2000);

		Date next1 = onceTrigger.nextExecutionTime(triggerContext);

		assert (next1.getTime() - testStart.getTime() <= 2000);

		triggerContext.update(next1, next1, next1);

		Date next2 = onceTrigger.nextExecutionTime(triggerContext);

		assertNull(next2);

	}
}
