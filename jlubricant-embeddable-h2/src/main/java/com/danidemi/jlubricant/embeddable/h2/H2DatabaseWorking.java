package com.danidemi.jlubricant.embeddable.h2;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danidemi.jlubricant.embeddable.JdbcDatabaseDescriptor;

public class H2DatabaseWorking implements JdbcDatabaseDescriptor {
	
	private static final Logger log = LoggerFactory.getLogger(H2DatabaseWorking.class);
	
	private H2Dbms master;
	
	static{
		try {
			Class.forName( org.h2.Driver.class.getName() );
		} catch (ClassNotFoundException e) {
			new RuntimeException(e);
		}
	}
	
	private H2DatabaseDescription descriptor;
	
	public void setMaster(H2Dbms master) {
		this.master = master;
	}


	public H2DatabaseWorking(H2DatabaseDescription descriptor, H2Dbms h2Ddms) {
		this.descriptor = descriptor;
		this.master = h2Ddms;
	}
	
	public void postStart() throws SQLException {
		
		// During post start phase, we run an actual connection to the database.
		// This will implicitly set username and pwd.
		String jdbcUrl = visit( new UrlVisitor().keepDbOpen() ).jdbcUrl();
		log.info("Post start initialization to {} {} {}", jdbcUrl, getUsername(), getPassword());
		Connection conn = DriverManager.getConnection(jdbcUrl, getUsername(), getPassword());
		conn.prepareStatement("SELECT 1+1").executeQuery();
		conn.close();
				
	}

	private UrlVisitor visit(UrlVisitor v) {
		descriptor.accept( v );
		master.accept( v );
		master.accept( v );
		return v;
	}
	
	public Connection newConnection() throws SQLException {
		Connection conn = DriverManager.
			    getConnection(getUrl(), descriptor.getUsername(), descriptor.getPassword());
		return conn;
		
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return newConnection();
	}

	public String getName() {
		return descriptor.getDbName();
	}


	@Override
	public String getUrl() {
		return visit( new UrlVisitor().onlyIfDatabaseExists() ).jdbcUrl();
	}


	@Override
	public String getPassword() {
		return descriptor.getPassword();
	}


	@Override
	public String getDriverClassName() {
		return descriptor.getDriverClassName();
	}


	@Override
	public String getUsername() {
		return descriptor.getUsername();
	}


	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		throw new UnsupportedOperationException();
	}


	@Override
	public PrintWriter getLogWriter() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getLoginTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public java.util.logging.Logger getParentLogger()
			throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
