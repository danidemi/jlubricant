package com.danidemi.jlubricant.embeddable.hsql.core;

import java.sql.Connection;
import java.sql.SQLException;

class ConnectedHsqlDatabaseStatus extends HsqlDatabaseStatus {

	private HsqlDatabaseDescriptor master;
		
	public ConnectedHsqlDatabaseStatus(HsqlDatabaseDescriptor master) {
		super();
		this.master = master;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return master.getFastConnection();
	}

}
