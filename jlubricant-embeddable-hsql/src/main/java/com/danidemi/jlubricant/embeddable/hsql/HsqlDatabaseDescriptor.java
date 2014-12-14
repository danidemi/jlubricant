package com.danidemi.jlubricant.embeddable.hsql;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hsqldb.jdbcDriver;
import org.hsqldb.lib.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danidemi.jlubricant.embeddable.JdbcDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.LocationConfiguration;
import com.danidemi.jlubricant.utils.hoare.Arguments;

public class HsqlDatabaseDescriptor implements JdbcDatabaseDescriptor, DataSource {
	
	private static Logger log = LoggerFactory.getLogger(HsqlDatabaseDescriptor.class);
	
	private final HsqlDatabaseStatus NOT_READY = new UnconnectedHsqlDatabaseStatus();
	private final HsqlDatabaseStatus READY = new ConnectedHsqlDatabaseStatus(this); 

	private final String dbName;
	private final Storage storage;
	private final Compatibility compatibility;
	private final Account desiredAccount;
	private HsqlDbms dbms;
	private HsqlDatabaseStatus currentStatus;
	private DataSource delegatedDataSource;
	
	private Account currentAccount;
		
	public HsqlDatabaseDescriptor(
			String dbName, 
			Storage storage,
			Compatibility compatibility, 
			String mainAccountUsername, 
			String mainAccountPassword) {
		
		super();
				
		Arguments.checkNotBlank(dbName, "DbName cannot be blank.");
		Arguments.checkNotBlank(mainAccountUsername, "Username cannot be blank.");
		Arguments.checkNotEquals(mainAccountUsername, "SA", "Sorry, you cannot specify 'SA' username.");
		Arguments.checkNotNull(mainAccountPassword, "Password cannot be null (but it can be blank).");
		Arguments.checkNotNull(compatibility, "Please provide a %s.", Compatibility.class.getSimpleName());
		Arguments.checkNotNull(storage, "Please provide a %s.", Storage.class.getSimpleName());
		
		currentAccount = new Account("SA","");
		desiredAccount = new Account(mainAccountUsername, mainAccountPassword);
		
		
		this.dbName = dbName;
		this.storage = storage;
		this.compatibility = compatibility;
		this.currentStatus = NOT_READY;
	}
	
	// ------------------------------------------------------------
	// lyfecycle
	// ------------------------------------------------------------
	
	/** Invoked during start up phase to give the database the chace to partecipate in the server configuration. */
	public void contributeToServerConfiguration(LocationConfiguration registration) {
		storage.contributeToServerConfiguration(this, registration);
	}	
	
	/** Invoked after server has started up. Here the database can connect to himself. */
	public void postStartSetUp() throws SQLException {
		
		if (compatibility != null) {
			compatibility.apply(this);
		}

		
			
			log.info("Creating new user {}", desiredAccount);
			try (Connection con = getPrivateConnection()) {
	
				ResultSet rs;
	
				// first of all'let's check whether the specified username already
				// exists.
				// PreparedStatement prepareStatement =
				// con.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS");
				// rs = prepareStatement.executeQuery();
				// while(rs.next()){
				// int columnCount = rs.getMetaData().getColumnCount();
				// for(int i=1; i<=columnCount; i++){
				// System.out.println(rs.getMetaData().getColumnName(i));
				// }
				// }
	
				PreparedStatement prepareStatement = con
						.prepareStatement("SELECT COUNT(*) FROM INFORMATION_SCHEMA.SYSTEM_USERS WHERE USER_NAME = ?");
				prepareStatement.setString(1, desiredAccount.getUsername());
				rs = prepareStatement.executeQuery();
				rs.next();
				boolean existingUser = rs.getLong(1) == 1L;
				rs.close();
				prepareStatement.close();
	
				PreparedStatement call;
				if (existingUser) {
					// throw new
					// IllegalArgumentException("Cannot change password to an existing user '"
					// + username + "'");
					 log.info("User exists, altering it to use the new password.");
					 call = con.prepareStatement("ALTER USER \"" + desiredAccount.getUsername() +
					 "\" SET PASSWORD '" + desiredAccount.getPassword() + "'");
					 call.execute();
				} else {
					log.info("User does not exists, granting it ADMIN privileges.");
					call = con.prepareStatement("CREATE USER \"" + desiredAccount.getUsername()
							+ "\" PASSWORD '" + desiredAccount.getPassword() + "' ADMIN");
					call.execute();
				}
	
				call = con
						.prepareCall("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS");
				rs = call.executeQuery();
				while (rs.next()) {
					log.info("User " + rs.getObject(1) + " " + rs.getObject(2));
				}
				
				currentAccount = desiredAccount;
	
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

		
		
		try (Connection con = getPrivateConnection()) {
			CallableStatement call = con
					.prepareCall("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS");
			ResultSet rs = call.executeQuery();
			while (rs.next()) {
				log.info("User " + rs.getObject(1) + " " + rs.getObject(2));
			}
		} catch (SQLException e) {
			new RuntimeException(e);
		}

	}	
	
	void goReady() {
		currentStatus = READY;
	}

	void goUnready() {
		currentStatus = NOT_READY;
	}	
	
	// ------------------------------------------------------------
	// properties
	// ------------------------------------------------------------				
	public String getDriverName() {
		return jdbcDriver.class.getName();
	}
	
	public String getDbName() {
		return dbName;
	}
	
	void setDbms(HsqlDbms hsqlDbms) {
		if(dbms!=null){
			throw new IllegalStateException("Already registered to a dbms");
		}
		this.dbms = hsqlDbms;
	}
	
	@Override
	public String getUrl() {
		return "jdbc:hsqldb:hsql://" + this.dbms.getHostName() + ":" + dbms.getPort() + "/" + this.dbName;
	}

	@Override
	public String getPassword() {
		return desiredAccount.getPassword();
	}

	@Override
	public String getDriverClassName() {
		return getDriverName();
	}

	@Override
	public String getUsername() {
		return desiredAccount.getUsername();
	}	

	
	// ------------------------------------------------------------
	// others
	// ------------------------------------------------------------
	void executePublicStm(String statement) {

		try (Connection conn = getConnection(); Statement stm = conn.createStatement()) {
			stm.execute(statement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	void executePrivateStm(String statement) {

		try (Connection conn = getPrivateConnection(); Statement stm = conn.createStatement()) {
			stm.execute(statement);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}	

	/** 
	 * Enables the syntax for the specific database.
	 * Under the hood it executes a {@code set database sql syntax <syntax> true } statement.
	 */
	void setSyntax(String syntax) {
		executePrivateStm("set database sql syntax "
				+ syntax
				+ " "
				+ "true");
	}

	/** 
	 * Enables MVCC.
	 */
	void setTransactionControl() {
		executePrivateStm("set database transaction control");
	}

	void setTransactionRollbackOnConflict(boolean setTransactionRollbackOnConflict) {
		executePrivateStm("set database transaction rollback on conflict "
				+ setTransactionRollbackOnConflict);
	}

	void setDatabaseSqlConcatNulls(boolean setDatabaseSqlConcatNulls) {
		executePrivateStm("set database sql concat nulls "
				+ setDatabaseSqlConcatNulls);
	}

	void setDatabaseSqlNullsFirst(boolean setDatabaseSqlNullsFirst) {
		executePrivateStm("set database sql nulls first "
				+ setDatabaseSqlNullsFirst);
	}

	void setDatabaseSqlUniqueNulls(boolean setDatabaseSqlUniqueNulls) {
		executePrivateStm("set database sql unique nulls "
				+ setDatabaseSqlUniqueNulls);
	}


		
	// Datasource

	void ensureFastDatasource() {
		if(delegatedDataSource==null){
			delegatedDataSource = dbms.getFastDataSource(this);
			if(delegatedDataSource == null){
				throw new RuntimeException("Unable to obtain datasource from dbms");
			}
		}
	}
	
	public PrintWriter getLogWriter() throws SQLException {
		ensureFastDatasource();
		return delegatedDataSource.getLogWriter();
	}


	public <T> T unwrap(Class<T> iface) throws SQLException {
		ensureFastDatasource();
		return delegatedDataSource.unwrap(iface);
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		ensureFastDatasource();
		delegatedDataSource.setLogWriter(out);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		ensureFastDatasource();
		return delegatedDataSource.isWrapperFor(iface);
	}

	public Connection getConnection() throws SQLException {
		return currentStatus.getConnection();
//		ensureFastDatasource();
//		return delegatedDataSource.getConnection();
	}
	
	/**
	 * During the start up phase, you need to have a connection to the db, even though it is not yet publicly declared 'connected'.
	 * @throws SQLException 
	 */
	private Connection getPrivateConnection() throws SQLException{
		try {
			Class.forName(jdbcDriver.class.getName());
		} catch (ClassNotFoundException e) {
			// this should really not happen
		}
		return DriverManager.getConnection(getUrl(), currentAccount.getUsername(), currentAccount.getPassword());
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		ensureFastDatasource();
		delegatedDataSource.setLoginTimeout(seconds);
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		ensureFastDatasource();
		return delegatedDataSource.getConnection(username, password);
	}

	public int getLoginTimeout() throws SQLException {
		ensureFastDatasource();
		return delegatedDataSource.getLoginTimeout();
	}

	public java.util.logging.Logger getParentLogger()
			throws SQLFeatureNotSupportedException {
		ensureFastDatasource();
		return delegatedDataSource.getParentLogger();
	}

	public DataSource delegatedDataSource() {
		return delegatedDataSource;
	}
	
	@Override
	public String toString() {
		return format("%s/%s/%s", dbName, this.compatibility, this.storage);
	}
	
	private class Account {
		private String password;
		private String username;
		public Account(String username, String password) {
			super();
			Arguments.checkNotBlank(username);
			Arguments.checkNotNull(password);
			this.password = password;
			this.username = username;
		}
		public String getUsername() {
			return username;
		}
		public String getPassword() {
			return password;
		}
		@Override
		public String toString() {
			return "[" + username + "/" + (password.length() == 0 ? "<blank>" : password) + "]";
		}
	}
	
}
