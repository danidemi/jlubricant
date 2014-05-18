package com.danidemi.jlubricant.embeddable.hsql;


public class OracleCompatibility extends Compatibility {

	public OracleCompatibility() {
		super();
	}

	@Override
	public void postStartSetUp(HsqlDatabase hsqlDatabase) {
		hsqlDatabase.setSyntax("ORA");
		hsqlDatabase.setTransactionControl();
		hsqlDatabase.setTransactionRollbackOnConflict(true);
		hsqlDatabase.setDatabaseSqlUniqueNulls(true);
		hsqlDatabase.setDatabaseSqlNullsFirst(false);
		hsqlDatabase.setDatabaseSqlConcatNulls(true);		
	}

}
