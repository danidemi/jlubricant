package com.danidemi.jlubricant.spring.context.trigger;

/**
 * Means that it was not possible to calculate the next triggering date.
 * @author danidemi
 *
 */
public class UndefinedTrigger extends RuntimeException {

	private static final long serialVersionUID = -6699975581250646003L;

	public UndefinedTrigger(String string) {
		super(string);
	}


}
