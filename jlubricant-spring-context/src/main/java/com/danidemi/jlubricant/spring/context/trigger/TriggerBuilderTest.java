package com.danidemi.jlubricant.spring.context.trigger;

import org.junit.Test;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.SimpleTriggerContext;


public class TriggerBuilderTest {

	@Test
	public void shouldBuildTriggers() {
		
		String[] expressions = new String[]{ 
				"fixed-rate:100",
				"fixed-delay:100",
				"fixed-rate:10,fixed-delay:10",
				"0 0 * 14 3 ?",
				"timerange:10:00:00->12:00:00->(fixed-rate:100)"
		};
		
		TriggerBuilder triggerBuilder = new TriggerBuilder();
		for (String expression : expressions) {
			Trigger trigger = triggerBuilder.build(expression);
			TriggerContext triggerContext = new SimpleTriggerContext();
			trigger.nextExecutionTime(triggerContext);
		}
		
	}
	
}
