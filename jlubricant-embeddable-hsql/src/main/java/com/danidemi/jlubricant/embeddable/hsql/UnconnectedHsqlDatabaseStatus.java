package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.Connection;

class UnconnectedHsqlDatabaseStatus extends HsqlDatabaseStatus {

	@Override
	public Connection getConnection() {
		throw new IllegalStateException("Not ready.");
	}

}
