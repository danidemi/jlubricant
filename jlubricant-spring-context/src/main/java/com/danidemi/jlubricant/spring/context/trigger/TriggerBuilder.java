package com.danidemi.jlubricant.spring.context.trigger;


import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

public class TriggerBuilder {

	protected static final Logger log = LoggerFactory.getLogger(TriggerBuilder.class);

	Trigger build(String triggerExpression) {

		// high priority for cron, without any particular format
		Trigger trigger = null;
		try {
			trigger = new CronTrigger(triggerExpression);
		} catch (Exception e) {
			// simply, not a cron!
		}
		
		// high priority for commas
		if (triggerExpression != null && trigger == null) {
			if (triggerExpression.contains(",")) {
				String[] split = triggerExpression.split(",");
				CombinedTrigger combinedTrigger = new CombinedTrigger();
				for (String triggerExp : split) {
					combinedTrigger.add(this.build(triggerExp));
				}
				return combinedTrigger;
			}
		}
		
		if (triggerExpression != null && trigger == null) {
			
			try{
				
				if (triggerExpression.startsWith("timerange:")) {
					String exp = triggerExpression.substring("timerange:".length());
					String[] split = exp.split("->");
					
					Trigger tg = null;
					String subExp = split[2];
					if(subExp.startsWith("(") && subExp.endsWith(")")){
						subExp = subExp.substring(1, subExp.length()-1);
						tg = build(subExp);
						if(tg == null){
							throw new IllegalArgumentException("subexpression is wrong");
						}
					}else{
						throw new IllegalArgumentException("expression is wrong");
					}
					
					TimelyRangedTrigger timelyRangedTrigger = new TimelyRangedTrigger();
					timelyRangedTrigger.setStart(split[0]);
					timelyRangedTrigger.setEnd(split[1]);
					timelyRangedTrigger.setDelegate(tg);
					trigger = timelyRangedTrigger;
					
				}
				
			}catch(Exception e){
				trigger = null;
			}
			
		}

		if (triggerExpression != null && trigger == null) {
			if (triggerExpression.startsWith("fixed-delay:")) {
				long delay = Long.parseLong(triggerExpression.split(":")[1]);
				PeriodicTrigger periodicTrigger = new PeriodicTrigger(delay);
				periodicTrigger.setFixedRate(false);
				trigger = periodicTrigger;
			}
		}

		if (triggerExpression != null && trigger == null) {
			if (triggerExpression.startsWith("fixed-rate:")) {
				long delay = Long.parseLong(triggerExpression.split(":")[1]);
				PeriodicTrigger periodicTrigger = new PeriodicTrigger(delay);
				periodicTrigger.setFixedRate(true);
				trigger = periodicTrigger;
			}
		}

		if (triggerExpression != null && trigger == null) {
			if ("never".equals(triggerExpression)) {
				trigger = new NeverTrigger();
			}
		}

		if (triggerExpression != null && trigger == null) {
			if ("once".equals(triggerExpression)) {
				trigger = new OnceTrigger();
			}
		}

		if (triggerExpression != null && trigger == null) {

			if (triggerExpression.startsWith("once-after:")) {
				long delay = Long.parseLong(triggerExpression.split(":")[1]);
				OnceTrigger onceTrigger = new OnceTrigger();
				onceTrigger.setAfter(delay);
				trigger = onceTrigger;
			}

		}

		if (triggerExpression != null && trigger == null) {

			try {

				if (triggerExpression.startsWith("every:")) {
					String[] tokens = triggerExpression.split(":");

					long delay = Long.parseLong(tokens[1].substring(0, tokens[1].length() - 3));
					String unit = tokens[1].substring(tokens[1].length() - 3);

					Integer mult = null;
					switch (unit) {
					case "sec":
						mult = 1000;
						break;
					case "min":
						mult = 60 * 1000;
						break;

					default:
						// not supported time
					}

					if (mult != null) {
						PeriodicTrigger pt = new PeriodicTrigger(delay * mult);
						pt.setFixedRate(true);
						trigger = pt;
					}
				}

			} catch (Exception e) {
				trigger = null;
			}


		}


		if (trigger == null) {
			throw new IllegalArgumentException(format("It was not possible to parse the trigger expression '%s'.", triggerExpression));
		}


		return trigger;


	}

}
