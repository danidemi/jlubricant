package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danidemi.jlubricant.embeddable.Database;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

public class HsqlDatabase implements Database {
	
	private static Logger log = LoggerFactory.getLogger(HsqlDatabase.class);

	private Storage storage;
	private Compatibility compatibility;
	private String dbName;
	private HsqlDbms dbms;
	private String password;
	private String username;
	

	public HsqlDatabase() {
		setCompatibility(new HsqlCompatibility());
		storage = new InProcessInMemory();
	}
	
	public HsqlDatabase(String name, Storage storage) {
		super();
		this.dbName = name;
		this.storage = storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	
	/**
	 * Change the database compatibility.
	 * If not set, {@link HsqlCompatibility} is intended.
	 */
	public void setCompatibility(Compatibility compatibility) {
		this.compatibility = compatibility;
	}
	
	/**
	 * Set the hsql db name.
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public String getName() {
		return dbName;
	}
	
	public Connection newConnection() throws SQLException {
		
		String protocol = storage.getProtocol();
		String location = storage.getLocation( dbName, dbms );
		String jdbcUrl1 = "jdbc:hsqldb:" + protocol + ":" + location;
		String jdbcUrl = jdbcUrl1;
		
		Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
		return conn;
	}

	public String getDriverName() {
		return "org.hsqldb.jdbcDriver";
	}

	void executeStm(String statement) {

		try (Connection conn = newConnection(); Statement stm = conn.createStatement()) {

			stm.execute(statement);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public boolean requireStandaloneServer() {
		return storage.requireStandaloneServer();
	}

	public void postStartSetUp() {
                if(compatibility != null){
                    compatibility.apply(this);
                }
		
		
		log.info("Creating new user {}/{}", username, password);
		
		try(Connection con = newConnection()){
			
			ResultSet rs;
			
			// first of all'let's check whether the specified username already exists.
//			PreparedStatement prepareStatement = con.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS");
//			rs = prepareStatement.executeQuery();
//			while(rs.next()){
//				int columnCount = rs.getMetaData().getColumnCount();
//				for(int i=1; i<=columnCount; i++){
//					System.out.println(rs.getMetaData().getColumnName(i));
//				}
//			}
			
			
			
			PreparedStatement prepareStatement = con.prepareStatement("SELECT COUNT(*) FROM INFORMATION_SCHEMA.SYSTEM_USERS WHERE USER_NAME = ?");
			prepareStatement.setString(1, username);
			rs = prepareStatement.executeQuery();
			rs.next();
			boolean existingUser = rs.getLong(1) == 1L;
			rs.close();
			prepareStatement.close();
			
			PreparedStatement call;
			if(existingUser){
				//throw new IllegalArgumentException("Cannot change password to an existing user '" + username + "'");
//				log.info("User exists, altering it to use the new password.");
//				call = con.prepareStatement("ALTER USER \"" + username + "\" SET PASSWORD '" + password + "'");
//				call.execute();			
			}else{
				log.info("User does not exists, granting it ADMIN privileges.");
				call = con.prepareStatement("CREATE USER \"" + username + "\" PASSWORD '" + password + "' ADMIN");
				call.execute();				
			}
			
			
			
			call = con.prepareCall("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS");
			rs = call.executeQuery();
			while(rs.next()){
				log.info("User " + rs.getObject(1) + " " + rs.getObject(2));
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		try(Connection con = newConnection()){
			CallableStatement call = con.prepareCall("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS");
			ResultSet rs = call.executeQuery();
			while(rs.next()){
				log.info("User " + rs.getObject(1) + " " + rs.getObject(2));
			}
		} catch (SQLException e) {
			new RuntimeException(e);
		}
		
		
		
	}

	public void register(Registration registration) {
		storage.register(this, registration);
		
	}

	void setDbms(HsqlDbms hsqlDbms) {
		if(dbms!=null){
			throw new IllegalStateException("Already registered to a dbms");
		}
		this.dbms = hsqlDbms;
	}

	/** 
	 * Enables the syntax for the specific database.
	 * Under the hood it executes a {@code set database sql syntax <syntax> true } statement.
	 */
	void setSyntax(String syntax) {
		executeStm("set database sql syntax "
				+ syntax
				+ " "
				+ "true");
	}

	/** 
	 * Enables MVCC.
	 */
	void setTransactionControl() {
		executeStm("set database transaction control");
	}

	void setTransactionRollbackOnConflict(boolean setTransactionRollbackOnConflict) {
		executeStm("set database transaction rollback on conflict "
				+ setTransactionRollbackOnConflict);
	}

	void setDatabaseSqlConcatNulls(boolean setDatabaseSqlConcatNulls) {
		executeStm("set database sql concat nulls "
				+ setDatabaseSqlConcatNulls);
	}

	void setDatabaseSqlNullsFirst(boolean setDatabaseSqlNullsFirst) {
		executeStm("set database sql nulls first "
				+ setDatabaseSqlNullsFirst);
	}

	void setDatabaseSqlUniqueNulls(boolean setDatabaseSqlUniqueNulls) {
		executeStm("set database sql unique nulls "
				+ setDatabaseSqlUniqueNulls);
	}

	@Override
	public String getUrl() {
		String protocol = storage.getProtocol();
		String location = storage.getLocation( dbName, dbms );
		String jdbcUrl = "jdbc:hsqldb:" + protocol + ":" + location;
		return jdbcUrl;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getDriverClassName() {
		return getDriverName();
	}

	@Override
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		
		if(username == null || username.trim().length() == 0){
			throw new IllegalArgumentException("Invalid user '" + username + "'");
		}
		
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
}
