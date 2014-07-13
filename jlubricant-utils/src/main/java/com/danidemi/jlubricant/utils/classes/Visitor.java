package com.danidemi.jlubricant.utils.classes;

public interface Visitor {

	/**
	 * Invoked when a {@link Class} is found and can be used. 
	 */
	public void onFoundClass(Class foundClass);
	
	/**
	 * Invoked when for whatever reason a class has been found but it cannot be used.
	 */
	public void onError(String className);
	
}
