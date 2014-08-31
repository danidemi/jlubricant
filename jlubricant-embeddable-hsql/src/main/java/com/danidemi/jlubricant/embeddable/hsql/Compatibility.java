package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.SQLException;

public abstract class Compatibility {
	
	/**
	 * Implementation should apply here all the modifications needed 
	 * to make the provided {@link HsqlDatabase} compatible.  
	 * @throws SQLException Applying a compatibility often involve issuing SQL commands, so, something wrong can happen.
	 */
	abstract public void apply(HsqlDatabase hsqlDatabase) throws SQLException;

}
