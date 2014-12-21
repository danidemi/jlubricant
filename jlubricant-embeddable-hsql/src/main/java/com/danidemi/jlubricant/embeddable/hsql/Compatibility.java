package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.SQLException;

/**
 * HSQL is able to "emulate" other databases. 
 * Implementations of this interface should be able to configure an HSQL database to emulate a specific database. 
 */
public abstract class Compatibility {
	
	/**
	 * Implementation should apply here all the modifications needed 
	 * to make the provided {@link HsqlDatabaseDescriptor} compatible.  
	 * @throws SQLException Applying a compatibility often involve issuing SQL commands, so, something wrong can happen.
	 */
	abstract public void apply(HsqlDatabaseDescriptor hsqlDatabase) throws SQLException;

}
