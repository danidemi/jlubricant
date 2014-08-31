package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.SQLException;

public class Db2Compatibility extends Compatibility {

	@Override
	public void apply(HsqlDatabase hsqlDatabase) throws SQLException {
		hsqlDatabase.setSyntax("DB2");
		hsqlDatabase.setTransactionControl();
	}

}
