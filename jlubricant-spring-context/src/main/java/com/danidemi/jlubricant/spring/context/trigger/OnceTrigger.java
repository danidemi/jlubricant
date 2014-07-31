package com.danidemi.jlubricant.spring.context.trigger;


import java.util.Date;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

/**
 * Trigger just once.
 * By default it triggers immediately, but if you set the "after" property, one can make it trigger after a while.
 */
public class OnceTrigger implements Trigger {

	private long after;

	public OnceTrigger() {
		after = 0;
	}

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		if (triggerContext.lastActualExecutionTime() == null) {
			return new Date(new Date().getTime() + after);
		} else {
			return null;
		}
	}

	/**
	 * @param millis How many seconds after initialization will that trigger start.
	 */
	public void setAfter(long millis) {
		after = millis;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder("once");
		if (after != 0) {
			string.append(" after ").append(after).append(" ms");
		}
		return string.toString();
	}

}
