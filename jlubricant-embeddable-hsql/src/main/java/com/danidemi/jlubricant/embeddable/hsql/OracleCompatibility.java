package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.SQLException;


public class OracleCompatibility extends Compatibility {

	public OracleCompatibility() {
		super();
	}

	@Override
	public void apply(HsqlDatabaseDescriptor hsqlDatabase) throws SQLException {
		hsqlDatabase.setSyntax("ORA");
		hsqlDatabase.setTransactionControl();
		hsqlDatabase.setTransactionRollbackOnConflict(true);
		hsqlDatabase.setDatabaseSqlUniqueNulls(true);
		hsqlDatabase.setDatabaseSqlNullsFirst(false);
		hsqlDatabase.setDatabaseSqlConcatNulls(true);		
	}

}
