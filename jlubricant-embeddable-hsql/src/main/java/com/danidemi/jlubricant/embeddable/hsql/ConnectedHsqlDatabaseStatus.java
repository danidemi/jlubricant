package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectedHsqlDatabaseStatus implements HsqlDatabaseStatus {

	private HsqlDatabase master;
		
	public ConnectedHsqlDatabaseStatus(HsqlDatabase master) {
		super();
		this.master = master;
	}

	@Override
	public Connection getConnection() throws SQLException {
		master.ensureFastDatasource();
		return master.delegatedDataSource().getConnection();
	}

}
