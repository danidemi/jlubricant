package com.danidemi.jlubricant.embeddable.hsql;

public abstract class Compatibility {

	/**
	 * Implementation should apply here all the modifications needed 
	 * to make the provided {@link HsqlDatabase} compatible.  
	 */
	abstract public void apply(HsqlDatabase hsqlDatabase);

}
