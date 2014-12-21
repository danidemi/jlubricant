package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.SQLException;

/**
 * HSQL is able to "emulate" other databases. 
 * Implementations of this interface should be able to configure an HSQL database to emulate a specific database. 
 */
public abstract class Compatibility implements PostStartContribution {
	
	/**
	 * Implementation should apply here all the modifications needed 
	 * to make the provided {@link HsqlDatabaseDescriptor} compatible.  
	 * @throws SQLException Applying a compatibility often involve issuing SQL commands, so, something wrong can happen.
	 */
	@Override
	abstract public void apply(HsqlDatabaseDescriptor hsqlDatabase) throws SQLException;
	
	/** 
	 * Enables the syntax for the specific database.
	 * Under the hood it executes a {@code set database sql syntax <syntax> true } statement.
	 */
	void setSyntax(HsqlDatabaseDescriptor hdd, String syntax) {
		hdd.executeStatement("set database sql syntax "
		+ syntax
		+ " "
		+ "true");
	}
	
	/** 
	 * Enables MVCC.
	 */
	void setTransactionControl(HsqlDatabaseDescriptor hdd) {
		hdd.executeStatement("set database transaction control");
	}

	void setTransactionRollbackOnConflict(HsqlDatabaseDescriptor hdd, boolean setTransactionRollbackOnConflict) {
		hdd.executeStatement("set database transaction rollback on conflict "
		+ setTransactionRollbackOnConflict);
	}

	void setDatabaseSqlConcatNulls(HsqlDatabaseDescriptor hdd, boolean setDatabaseSqlConcatNulls) {
		hdd.executeStatement("set database sql concat nulls "
		+ setDatabaseSqlConcatNulls);
	}

	void setDatabaseSqlNullsFirst(HsqlDatabaseDescriptor hdd, boolean setDatabaseSqlNullsFirst) {
		hdd.executeStatement("set database sql nulls first "
		+ setDatabaseSqlNullsFirst);
	}

	void setDatabaseSqlUniqueNulls(HsqlDatabaseDescriptor hdd, boolean setDatabaseSqlUniqueNulls) {
		hdd.executeStatement("set database sql unique nulls "
		+ setDatabaseSqlUniqueNulls);
	}
	
}
