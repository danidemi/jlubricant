package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.Connection;

public class UnconnectedHsqlDatabaseStatus implements HsqlDatabaseStatus {

	@Override
	public Connection getConnection() {
		throw new IllegalStateException("Not ready.");
	}

}
