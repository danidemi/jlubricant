package com.danidemi.jlubricant.embeddable.hsql.core.compatibility;

import java.sql.SQLException;

import com.danidemi.jlubricant.embeddable.hsql.core.Compatibility;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDatabaseDescriptor;

public class Db2Compatibility extends Compatibility {

	@Override
	public void apply(HsqlDatabaseDescriptor hsqlDatabase) throws SQLException {
		setSyntax(hsqlDatabase, "DB2");
		setTransactionControl(hsqlDatabase);
	}

}
