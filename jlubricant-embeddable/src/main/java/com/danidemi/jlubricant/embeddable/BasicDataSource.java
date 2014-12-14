package com.danidemi.jlubricant.embeddable;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class BasicDataSource implements DataSource {

	private PrintWriter pw;
	private int loginTimeoutInSeconds;
	private JdbcDatabaseDescriptor db;
	
	public BasicDataSource(JdbcDatabaseDescriptor db) {
		this.db = db;
		loginTimeoutInSeconds = 10;
		pw = null;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return this.pw; 
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		
		if(pw == out) return;
		
		out.flush();
		out = pw;
		
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		this.loginTimeoutInSeconds = seconds;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return loginTimeoutInSeconds;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("Sorry, nothing to unwrap. Try to invoke isWrapperFor() before invoking unwrap().");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(db.getUsername(), db.getPassword());
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		try {
			Class.forName( db.getDriverClassName() );
		} catch (ClassNotFoundException e) {
			throw new SQLException(e);
		}
		return DriverManager.getConnection(db.getUrl(), username, password);
	}

}
