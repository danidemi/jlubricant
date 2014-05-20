package com.danidemi.jlubricant.embeddable.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.danidemi.jlubricant.embeddable.ServerStartException;

public class H2DatabaseDescription {

	private String dbName;

	public H2DatabaseDescription(String dbName) {
		this.dbName = dbName;
	}
	
	public String getDbName() {
		return dbName;
	}
	
	Connection newConnection() throws ClassNotFoundException, SQLException {

		String jdbcUrl = "jdbc:h2:tcp://localhost/mem:" + dbName + ";IFEXISTS=TRUE";
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.
			    getConnection(jdbcUrl, "sa", "");
		return conn;
		
	}



}
