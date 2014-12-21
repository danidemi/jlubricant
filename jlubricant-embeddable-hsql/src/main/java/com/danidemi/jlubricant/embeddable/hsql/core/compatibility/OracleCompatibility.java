package com.danidemi.jlubricant.embeddable.hsql.core.compatibility;

import java.sql.SQLException;

import com.danidemi.jlubricant.embeddable.hsql.core.Compatibility;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDatabaseDescriptor;


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
