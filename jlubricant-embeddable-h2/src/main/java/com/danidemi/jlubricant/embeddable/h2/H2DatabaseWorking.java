package com.danidemi.jlubricant.embeddable.h2;

import java.sql.Connection;
import java.sql.DriverManager;

import com.danidemi.jlubricant.embeddable.Database;
import com.danidemi.jlubricant.embeddable.ServerStartException;

public class H2DatabaseWorking implements Database {

	private H2DatabaseDescription descriptor;

	public H2DatabaseWorking(H2DatabaseDescription descriptor) {
		this.descriptor = descriptor;
	}
	
	public void postStart() throws ServerStartException {
		try {
			String jdbcUrl = "jdbc:h2:tcp://localhost/mem:" + descriptor.getDbName() + ";DB_CLOSE_DELAY=-1";
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.
					getConnection(jdbcUrl, "sa", "");
			conn.close();
		} catch (Exception e) {
			throw new ServerStartException(e);
		}
		
	}

	public String getName() {
		return descriptor.getDbName();
	}

}
