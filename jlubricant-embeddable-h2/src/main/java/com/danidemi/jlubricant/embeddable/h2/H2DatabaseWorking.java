package com.danidemi.jlubricant.embeddable.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.danidemi.jlubricant.embeddable.Database;
import com.danidemi.jlubricant.embeddable.ServerStartException;

public class H2DatabaseWorking implements Database {
	
	private H2Ddms master;
	
	static{
		try {
			Class.forName( org.h2.Driver.class.getName() );
		} catch (ClassNotFoundException e) {
			new RuntimeException(e);
		}
	}
	
	private H2DatabaseDescription descriptor;
	
	public void setMaster(H2Ddms master) {
		this.master = master;
	}


	public H2DatabaseWorking(H2DatabaseDescription descriptor, H2Ddms h2Ddms) {
		this.descriptor = descriptor;
		this.master = h2Ddms;
	}
	
	public void postStart() throws ServerStartException {
		
		UrlVisitor visit = visit( new UrlVisitor() );
		
		try {
			String jdbcUrl = visit.jdbcUrl() + ";DB_CLOSE_DELAY=-1";
			Connection conn = DriverManager.
					getConnection(jdbcUrl, "sa", "");
			conn.close();
		} catch (Exception e) {
			throw new ServerStartException(e);
		}
		
	}

	private UrlVisitor visit(UrlVisitor v) {
		descriptor.accept( v );
		master.accept( v );
		master.accept( v );
		return v;
	}
	
	public Connection newConnection() throws SQLException {
		
		UrlVisitor visit = visit( new UrlVisitor() );		

		String jdbcUrl = visit.jdbcUrl() + ";IFEXISTS=TRUE";
		
		Connection conn = DriverManager.
			    getConnection(jdbcUrl, "sa", "");
		return conn;
		
	}

	public String getName() {
		return descriptor.getDbName();
	}

}
