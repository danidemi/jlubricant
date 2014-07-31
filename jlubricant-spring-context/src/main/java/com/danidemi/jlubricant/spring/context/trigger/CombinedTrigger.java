package com.danidemi.jlubricant.spring.context.trigger;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

/**
 * The {@link CombinedTrigger} is able to merge multiple triggers. 
 * A {@link CombinedTrigger} triggers when any of its underlying trigger triggers.
 * @author danidemi
 */
public final class CombinedTrigger implements Trigger {

	private final List<Trigger> triggers;

	public CombinedTrigger() {
		triggers = new ArrayList<>(0);
	}

	@Override
	public final Date nextExecutionTime(TriggerContext triggerContext) {
		Date next = null;
		for (Trigger trigger : triggers) {
			Date nextExecutionTime = trigger.nextExecutionTime(triggerContext);
			if (next == null) {
				next = nextExecutionTime;
			} else {
				if (nextExecutionTime.before(next)) {
					next = nextExecutionTime;
				}
			}
		}
		return next;
	}

	public final void add(Trigger trigger) {
		this.triggers.add(trigger);
	}

	@Override
	public final String toString() {
		return new StringBuilder("earlier among ").append(triggers).toString();
	}

}
