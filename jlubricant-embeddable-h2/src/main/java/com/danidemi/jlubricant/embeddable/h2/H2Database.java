package com.danidemi.jlubricant.embeddable.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.danidemi.jlubricant.embeddable.ServerStartException;

public class H2Database {

	private String dbName;

	public H2Database(String dbName) {
		this.dbName = dbName;
	}
	
	Connection newConnection() throws ClassNotFoundException, SQLException {

		String jdbcUrl = "jdbc:h2:tcp://localhost/mem:" + dbName + ";IFEXISTS=TRUE";
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.
			    getConnection(jdbcUrl, "sa", "");
		return conn;
		
	}

	public void postStart() throws ServerStartException {
		try {
			String jdbcUrl = "jdbc:h2:tcp://localhost/mem:" + dbName + ";DB_CLOSE_DELAY=-1";
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.
					getConnection(jdbcUrl, "sa", "");
			conn.close();
		} catch (Exception e) {
			throw new ServerStartException(e);
		}
		
	}

}
