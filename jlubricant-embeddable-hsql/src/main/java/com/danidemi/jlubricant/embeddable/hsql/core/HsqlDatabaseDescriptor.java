package com.danidemi.jlubricant.embeddable.hsql.core;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Deque;
import java.util.LinkedList;

import javax.sql.DataSource;

import org.hsqldb.jdbcDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danidemi.jlubricant.embeddable.Dbms;
import com.danidemi.jlubricant.embeddable.JdbcDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.database.core.Account;
import com.danidemi.jlubricant.embeddable.database.core.BaseAccount;
import com.danidemi.jlubricant.embeddable.database.core.ObservableAccount;
import com.danidemi.jlubricant.embeddable.database.core.Observer;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms.LocationConfiguration;
import com.danidemi.jlubricant.utils.hoare.Arguments;

public class HsqlDatabaseDescriptor implements JdbcDatabaseDescriptor, DataSource {
	
	private static Logger log = LoggerFactory.getLogger(HsqlDatabaseDescriptor.class);
	
	private final HsqlDatabaseStatus NOT_READY = new UnconnectedHsqlDatabaseStatus();
	private final HsqlDatabaseStatus READY = new ConnectedHsqlDatabaseStatus(this); 

	private final String dbName;
	private final Storage storage;
	
	/** The account that should be used to access the database. During startup phase the two accounts could not match. 
	 * I.e. during startup the account to use is the default account ("SA" for HSQL) and just after creating the desired user, it can be used. */
	private ObservableAccount currentAccount;
	
	private HsqlDbms dbms;
	private HsqlDatabaseStatus currentStatus;
	private DataSource delegatedDataSource;
	
	private Deque<PostStartContribution> postStartContributions;
	
	/**
	 * @param dbName
	 * @param storage
	 * @param compatibility
	 * @param mainAccountUsername	The username one would use to connect after startup.
	 * @param mainAccountPassword	The password one would use to connect after startup.
	 */
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
		this.currentStatus = NOT_READY;
				
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
		
		postStartContributions = new LinkedList<PostStartContribution>();
		postStartContributions.push(compatibility);
		postStartContributions.push(new SetUserContribution(new BaseAccount(mainAccountUsername, mainAccountPassword)));
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
		
		PostStartContribution poll;
		while( (poll = postStartContributions.poll()) != null){
			poll.apply(this);
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
		return format("%s/%s", dbName, this.storage);
	}
	
	public void setAccount(Account newAccount) {
		this.currentAccount.set(newAccount);
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
