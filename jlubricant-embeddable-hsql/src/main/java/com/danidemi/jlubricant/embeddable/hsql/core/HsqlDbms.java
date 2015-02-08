package com.danidemi.jlubricant.embeddable.hsql.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.dbcp.BasicDataSource;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danidemi.jlubricant.embeddable.Dbms;
import com.danidemi.jlubricant.embeddable.EmbeddableServer;
import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;
import com.danidemi.jlubricant.slf4j.utils.LubricantLoggerWriter;
import com.danidemi.jlubricant.slf4j.utils.OneLogLineForEachFlush;
import com.danidemi.jlubricant.slf4j.utils.Replace;
import com.danidemi.jlubricant.utils.hoare.Arguments;

/**
 * An embeddable database engine based on Hsql.
 * 
 * @author danidemi
 */
public class HsqlDbms implements EmbeddableServer, Dbms {

	/** HSQL default port, {@literal DEFAULT_PORT}. */
	private static final Integer DEFAULT_PORT = 9001;

	/** HSQL default ip binding. Null means to use default hsql binding rules. */
	private static final String DEFAULT_IP = null;

	/** Logger */
	private static Logger log = LoggerFactory.getLogger(HsqlDbms.class);

	/** Logger that receives log messages genenrated by HSQL itself. */ 
	//private static Logger hsqlWriterLogger = LoggerFactory.getLogger(HsqlDbms.class.getName() + ".hsql");

	/**
	 * A Writer that can be provided to hsql and redirects messages to a proper
	 * logger.
	 * This is needed because HSQL logs through a {@link PrintWriter}.
	 */
	private static LubricantLoggerWriter lubricantLoggerWriter = new LubricantLoggerWriter(
			LoggerFactory.getLogger(HsqlDbms.class.getName() + ".hsql"), 
			new Replace(new OneLogLineForEachFlush(), "\\[[\\w\\W]*\\]: ", ""), com.danidemi.jlubricant.slf4j.Logger.TRACE);

	/** The Hsql server. */
	private Server server;

	/** The list of HSQL databases that will be run by this {@link HsqlDbms}. */
	private ArrayList<HsqlDatabaseDescriptor> dbs;

	/** Service that a database should use to participate in the server configuration. */	
	public static interface LocationConfiguration {
		
		/** Allow to specify the location of the HSQL database. */
		void setLocation(String dbName, String dbLocation);
		
	}

	private Status currentStatus;

	/**
	 * The port the database should bind to. {@code null} means the server will
	 * bind to the HSQL default port.
	 */
	private Integer definedPort = null;
	private String address;

	/** 
	 * Instantiate an {@link HsqlDbms} that run a set of given databases.
	 * @param Each descriptor define the parameters of each db to run. 
	 * */
	public HsqlDbms(List<HsqlDatabaseDescriptor> descriptors) {
		Arguments.checkNotEmpty(descriptors, "Please, provide a not null not empty list of databases.");
		this.dbs = new ArrayList<HsqlDatabaseDescriptor>();
		server = null;
		currentStatus = new StoppedStatus(this);
		for (HsqlDatabaseDescriptor hsqlDatabaseDescriptor : descriptors) {
			add(hsqlDatabaseDescriptor);
		}
	}
		
	/** 
	 * Instantiate a {@link HsqlDbms} that just run one database.
	 * @param Each descriptor define the parameters of each db to run. 
	 * */
	public HsqlDbms(HsqlDatabaseDescriptor... descriptors) {
		this(Arrays.asList(descriptors));
	}	

	// ===============================================================
	// Lyfecycle
	// ===============================================================



	/**
	 * Synchronously start the server.
	 */
	@Override
	public void start() throws ServerException {
		currentStatus.onStart();
	}

	/**
	 * Synchronously stop the server.
	 */	
	@Override
	public void stop() throws ServerException {
		currentStatus.onStop();
	}

	void stopEngine() throws ServerStopException {

		server.shutdown();

		try {

			log.info("Waiting for hsql engine to stop...");
			int state = server.getState();
			while (state != 16) {
				log.trace(
						"Waiting '16' as confirmation from hsql engine, got '{}:{}'.",
						state, server.getStateDescriptor());
				Thread.yield();
				Thread.sleep(100);
				state = server.getState();
			}
			log.info("Hsql engine stopped.");

			log.info("Closing logger...");
			lubricantLoggerWriter.close();
			log.info("Logger closed.");

		} catch (Exception e) {

			throw new ServerStopException(e);

		}
	}

	// ===============================================================
	// Management
	// ===============================================================

	void startEngine() throws ServerStartException {

		// setting up the server
		final boolean isDaemon = true;
		final boolean isSilent = false;

		final HsqlProperties hsqlProp = new HsqlProperties();

		// gives the chance to the descriptors to contribute to the server configuration.
		final AtomicInteger dbCounter = new AtomicInteger(0);
		for (HsqlDatabaseDescriptor db : dbs) {
			
			db.contributeToServerConfiguration(new LocationConfiguration() {

				@Override
				public void setLocation(String dbName, String location) {

					log.info("Registering db #{} : '{}' stored on '{}'", dbCounter, dbName, location);
					int dbIndex = dbCounter.getAndAdd(1);
					if (!(location.startsWith("file:") || location.startsWith("res:") || location
							.startsWith("mem:"))) {
						throw new IllegalArgumentException(location);
					}
					
					hsqlProp.setProperty("server.database." + dbIndex, location);
					hsqlProp.setProperty("server.dbname." + dbIndex, dbName);
				}

			});

		}
		// avoid to open a db remotely
		server = new Server();
		hsqlProp.setProperty("server.remote_open", false);
		try {
			server.setProperties(hsqlProp);
		} catch (IOException | AclFormatException e1) {
			throw new ServerStartException(e1);
		}

		server.setLogWriter(lubricantLoggerWriter.asPrintWriter());
		server.setDaemon(isDaemon);
		server.setSilent(isSilent);
		server.setTrace(false);
		server.setNoSystemExit(true);

		if (definedPort != null) {
			server.setPort(definedPort);
		}
		if (this.address != null) {
			server.setAddress(this.address);
		}

		for (int i = 0; i < this.dbs.size(); i++) {
			log.info("db " + i);
			log.info(server.getDatabaseType(i));
			log.info(server.getDatabaseName(i, true));
			log.info(server.getDatabasePath(i, true));
		}

		// dump config
		log.info("Starting HSQL standalone DBMS...");
		log.info("is daemon: {}", isDaemon);
		log.info("is silent: {}", isSilent);
		log.info("port: {} ", (definedPort != null ? definedPort
				: "HSQL default"));
		log.info("address: {} ", (address != null ? address : "HSQL default"));

		log.info("Waiting for hsql engine to start...");
		
		
		Integer state = server.start();
		Throwable serverError = server.getServerError();
		if(serverError!=null){
			throw new ServerStartException(serverError);
		}			
		do {
			state = server.getState();
			Thread.yield();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// nothing special
			}			
		}while(state != 1);
		
		log.info("Hsql engine started.");

		try{
			log.info("Starting databases post setup...");
			for (int i = 0; i < dbs.size(); i++) {
				final HsqlDatabaseDescriptor db = dbs.get(i);
				log.info("Post start up for db '{}', {}/{}.", db.getDbName(), i+1, dbs.size());
				db.postStartSetUp();
				db.goReady();
			}
			log.info("Post setup completed.");			
		}catch(Exception e){
			throw new ServerStartException("An error occurred during post setup.", e);
		}

		log.info("Dump connection strings:");
		for (HsqlDatabaseDescriptor db : dbs) {
			log.info("Connection to db {}: {}, account {}/{} ", db.getDbName(),
					db.getUrl(), db.getDbName(), db.getPassword());
		}

		log.info("HSQL standalone DBMS is ready");

	}

	/**
	 * Returns a DataSource on the given database.
	 * 
	 * @deprecated Databases are {@link DataSource} now.
	 * */
	@Override
	@Deprecated
	public DataSource dataSourceByName(String dbName) {
		return new com.danidemi.jlubricant.embeddable.BasicDataSource(
				dbByName(dbName));
	}

	/**
	 * Returns the {@link HsqlDatabaseDescriptor} with the given name.
	 */
	@Override
	public HsqlDatabaseDescriptor dbByName(final String dbName) {
		Collection<HsqlDatabaseDescriptor> select = CollectionUtils.select(dbs,
				new Predicate<HsqlDatabaseDescriptor>() {

					@Override
					public boolean evaluate(HsqlDatabaseDescriptor db) {
						return dbName.equals(db.getDbName());
					}

				});
		if (CollectionUtils.size(select) == 0) {
			throw new IllegalArgumentException(
					"There are no databases called '" + dbName + "'");
		}
		if (CollectionUtils.size(select) >= 2) {
			throw new IllegalArgumentException(
					"More than one database is called '" + dbName + "'");
		}
		return CollectionUtils.extractSingleton(select);
	}

	/**
	 * Return a quick datasource for the given hsqldatabase.
	 */
	DataSource getFastDataSource(HsqlDatabaseDescriptor hsqlDatabase) {
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(hsqlDatabase.getDriverClassName());
		bds.setUsername(hsqlDatabase.getUsername());
		bds.setPassword(hsqlDatabase.getPassword());
		bds.setUrl(hsqlDatabase.getUrl());
		return bds;
	}
	
	/**
	 * Close the fast datasource.

	 */
	public void closeFastDataSource(DataSource ds) throws SQLException {
		BasicDataSource bds = (BasicDataSource) ds;
		bds.close();
	}

	// ===============================================================
	// Properties
	// ===============================================================

	public String getHostName() {
		return address != null ? address : "localhost";
	}

	public int getPort() {
		return definedPort == null ? DEFAULT_PORT : definedPort;
	}

	public void setPort(int port) {
		currentStatus.onPropertyChange();
		this.definedPort = port;
	}

	public String getIp() {
		return address != null ? address : DEFAULT_IP;
	}

	public void setIp(String ip) {
		currentStatus.onPropertyChange();
		this.address = ip;
	}

	private boolean add(HsqlDatabaseDescriptor e) {
		currentStatus.onPropertyChange();
		e.setDbms(this);
		return dbs.add(e);
	}
	
	private void addMultiple(HsqlDatabaseDescriptor... dbs) {
		for (HsqlDatabaseDescriptor hsqlDatabase : dbs) {
			add(hsqlDatabase);
		}
		
	}

	public void setDatabases(List<HsqlDatabaseDescriptor> dbs) {
		currentStatus.onPropertyChange();
		for (HsqlDatabaseDescriptor hsqlDatabase : dbs) {
			add(hsqlDatabase);
		}
	}

	void transitionTo(Status newStatus) {
		this.currentStatus = newStatus;
	}

}
