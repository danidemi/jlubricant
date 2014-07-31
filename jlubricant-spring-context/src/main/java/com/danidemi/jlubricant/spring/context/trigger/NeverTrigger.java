package com.danidemi.jlubricant.spring.context.trigger;

import java.util.Date;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.config.Task;

/**
 * This trigger never triggers.
 * It can be useful if you want a {@link Task} bean to remain in the context without being triggered.
 * @author danidemi
 */
public final class NeverTrigger implements Trigger {

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		return null;
	}

	@Override
	public String toString() {
		return "never";
	}

}
