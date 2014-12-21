package com.danidemi.jlubricant.embeddable.hsql.core;

import java.sql.SQLException;


public class OracleCompatibility extends Compatibility {

	public OracleCompatibility() {
		super();
	}

	@Override
	public void apply(HsqlDatabaseDescriptor hsqlDatabase) throws SQLException {
		setSyntax(hsqlDatabase, "ORA");
		setTransactionControl(hsqlDatabase);
		setTransactionRollbackOnConflict(hsqlDatabase, true);
		setDatabaseSqlUniqueNulls(hsqlDatabase, true);
		setDatabaseSqlNullsFirst(hsqlDatabase, false);
		setDatabaseSqlConcatNulls(hsqlDatabase, true);		
	}

}
