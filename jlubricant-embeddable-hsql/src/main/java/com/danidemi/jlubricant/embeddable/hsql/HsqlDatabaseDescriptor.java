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

import com.danidemi.jlubricant.embeddable.Dbms;
import com.danidemi.jlubricant.embeddable.JdbcDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.database.core.Account;
import com.danidemi.jlubricant.embeddable.database.core.BaseAccount;
import com.danidemi.jlubricant.embeddable.database.core.ObservableAccount;
import com.danidemi.jlubricant.embeddable.database.core.Observer;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.LocationConfiguration;
import com.danidemi.jlubricant.utils.hoare.Arguments;

public class HsqlDatabaseDescriptor implements JdbcDatabaseDescriptor, DataSource {
	
	private static Logger log = LoggerFactory.getLogger(HsqlDatabaseDescriptor.class);
	
	private final HsqlDatabaseStatus NOT_READY = new UnconnectedHsqlDatabaseStatus();
	private final HsqlDatabaseStatus READY = new ConnectedHsqlDatabaseStatus(this); 

	private final String dbName;
	private final Storage storage;
	private final Compatibility compatibility;
	
	/** The account that will be authorized to access the database once the db completes the start up phase. */
	private final BaseAccount desiredAccount;
	
	/** The account that should be used to access the database. During startup phase the two accounts could not match. 
	 * I.e. during startup the account to use is the default account ("SA" for HSQL) and just after creating the desired user, it can be used. */
	private ObservableAccount currentAccount;
	
	private HsqlDbms dbms;
	private HsqlDatabaseStatus currentStatus;
	private DataSource delegatedDataSource;
	
		
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
		
		this.dbName = dbName;
		this.storage = storage;
		this.compatibility = compatibility;
		this.currentStatus = NOT_READY;
		
		desiredAccount = new BaseAccount(mainAccountUsername, mainAccountPassword);
		
		currentAccount = new ObservableAccount( new BaseAccount("SA","") );
		currentAccount.registerObserver(new Observer() {
			
			@Override
			public void update() {
				// if the account changed, we should recreate the data source.
				if(delegatedDataSource!=null){
					try {
						dbms.closeFastDataSource(delegatedDataSource);
					} catch (SQLException e) {
						log.warn("It was not possible to close the current datasource, due to: {}. Ignoring", e.getMessage(), e);
					} finally {
						delegatedDataSource = null;
					}
				}
			}
		});
		
		delegatedDataSource = null;
	}
	
	
	
	// ------------------------------------------------------------
	// lyfecycle
	// ------------------------------------------------------------
	
	/** Invoked during start up phase to give the database the chance to participate in the server configuration. */
	public void contributeToServerConfiguration(LocationConfiguration registration) {
		storage.contributeToServerConfiguration(this, registration);
	}	
	
	/** Invoked after server has started up. Here the database can connect to himself. */
	public void postStartSetUp() throws SQLException {
		
		if (compatibility != null) {
			compatibility.apply(this);
		}

		
			
			log.info("Creating new user {}", desiredAccount);
			try (Connection con = getFastConnection()) {
	
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
				
				currentAccount.set(desiredAccount);
	
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

		
		
		try (Connection con = getFastConnection()) {
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
		return currentAccount.getPassword();
	}

	@Override
	public String getDriverClassName() {
		return getDriverName();
	}

	@Override
	public String getUsername() {
		//return desiredAccount.getUsername();
		return currentAccount.getUsername();
	}
	
	
	// ------------------------------------------------------------
	// delegate data source
	// ------------------------------------------------------------	
	
	/**
	 * Returns (and init if needed) a datasource backed by {@link Dbms}'s {@link DataSource}. 
	 */
	DataSource getFastDatasource() {
		if(delegatedDataSource==null){
			delegatedDataSource = dbms.getFastDataSource(this);
			if(delegatedDataSource == null){
				throw new RuntimeException("Unable to obtain datasource from dbms");
			}
		}
		return delegatedDataSource;
	}
	
	/**
	 * Return a {@link Connection} obtained from the fast datasource.
	 * @throws SQLException 
	 */
	Connection getFastConnection() throws SQLException{
		return getFastDatasource().getConnection();		
	}	

	@Override
	public String toString() {
		return format("%s/%s/%s", dbName, this.compatibility, this.storage);
	}
	
	// ------------------------------------------------------------
	// others
	// ------------------------------------------------------------
	void executeStatement(String statement) {

		try (Connection conn = getFastConnection(); Statement stm = conn.createStatement()) {
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
		executeStatement("set database sql syntax "
		+ syntax
		+ " "
		+ "true");
	}

	/** 
	 * Enables MVCC.
	 */
	void setTransactionControl() {
		executeStatement("set database transaction control");
	}

	void setTransactionRollbackOnConflict(boolean setTransactionRollbackOnConflict) {
		executeStatement("set database transaction rollback on conflict "
		+ setTransactionRollbackOnConflict);
	}

	void setDatabaseSqlConcatNulls(boolean setDatabaseSqlConcatNulls) {
		executeStatement("set database sql concat nulls "
		+ setDatabaseSqlConcatNulls);
	}

	void setDatabaseSqlNullsFirst(boolean setDatabaseSqlNullsFirst) {
		executeStatement("set database sql nulls first "
		+ setDatabaseSqlNullsFirst);
	}

	void setDatabaseSqlUniqueNulls(boolean setDatabaseSqlUniqueNulls) {
		executeStatement("set database sql unique nulls "
		+ setDatabaseSqlUniqueNulls);
	}


		
	// Datasource
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		getFastDatasource();
		return delegatedDataSource.getLogWriter();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		getFastDatasource();
		return delegatedDataSource.unwrap(iface);
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		getFastDatasource();
		delegatedDataSource.setLogWriter(out);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		getFastDatasource();
		return delegatedDataSource.isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return currentStatus.getConnection();
	}
	
	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		getFastDatasource();
		delegatedDataSource.setLoginTimeout(seconds);
	}

	@Override
	public Connection getConnection(String username, String password)
			throws SQLException {
		getFastDatasource();
		return delegatedDataSource.getConnection(username, password);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		getFastDatasource();
		return delegatedDataSource.getLoginTimeout();
	}

	@Override
	public java.util.logging.Logger getParentLogger()
			throws SQLFeatureNotSupportedException {
		getFastDatasource();
		return delegatedDataSource.getParentLogger();
	}
		
}
