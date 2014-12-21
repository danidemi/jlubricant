package com.danidemi.jlubricant.embeddable.hsql.core;

import java.sql.SQLException;

public class Db2Compatibility extends Compatibility {

	@Override
	public void apply(HsqlDatabaseDescriptor hsqlDatabase) throws SQLException {
		setSyntax(hsqlDatabase, "DB2");
		setTransactionControl(hsqlDatabase);
	}

}
