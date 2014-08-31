package com.danidemi.jlubricant.embeddable.hsql;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.dbcp.BasicDataSource;
import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerProperties;
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

import static org.apache.commons.collections4.CollectionUtils.*;

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

	/** Classic logger */
	private static Logger log = LoggerFactory.getLogger(HsqlDbms.class);

	/** Classic logger that receives log messages geenrated by HSQL itself */
	private static Logger hsqlWriterLogger = LoggerFactory
			.getLogger(HsqlDbms.class.getName() + ".hsql");

	/**
	 * A Writer that can be provided to hsql and redirects messages to a proper
	 * logger.
	 */
	private static LubricantLoggerWriter lubricantLoggerWriter = new LubricantLoggerWriter(
			hsqlWriterLogger, new Replace(new OneLogLineForEachFlush(),
					"\\[[\\w\\W]*\\]: ", ""),
			com.danidemi.jlubricant.slf4j.Logger.TRACE);

	/** The Hsql server. */
	private Server server;

	/** The list of HSQL databases that will be run by this {@link HsqlDbms}. */
	private ArrayList<HsqlDatabase> dbs;

	static interface Registration {
		void register(String dbName, String location);
	}

	private Status currentStatus;

	/**
	 * The port the database should bind to. {@code null} means the server will
	 * bind to the HSQL default port.
	 */
	private Integer definedPort = null;
	private String address;

	public HsqlDbms() {
		dbs = new ArrayList<>(0);
		server = null;
		currentStatus = new StoppedStatus(this);
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

	@Override
	public void stop() throws ServerException {

		currentStatus.onStop();

	}

	private static void register(HsqlProperties hsqlProp, int dbIndex, String name,
			String path) {

		if (!(path.startsWith("file:") || path.startsWith("res:") || path
				.startsWith("mem:"))) {
			throw new IllegalArgumentException(path);
		}

		hsqlProp.setProperty("server.database." + dbIndex, path);
		hsqlProp.setProperty("server.dbname." + dbIndex, name);
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

		// Register the databases
		final AtomicInteger dbCounter = new AtomicInteger(0);
		for (HsqlDatabase db : dbs) {

			db.register(new Registration() {

				@Override
				public void register(String dbName, String location) {

					log.info("Registering db #{} : '{}' stored on '{}'",
							dbCounter, dbName, location);
					HsqlDbms.register(hsqlProp, dbCounter.getAndAdd(1), dbName,
							location);
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
		lubricantLoggerWriter = new LubricantLoggerWriter(hsqlWriterLogger,
				new Replace(new OneLogLineForEachFlush(), "\\[[\\w\\W]*\\]: ",
						""), com.danidemi.jlubricant.slf4j.Logger.TRACE);
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
		int state = server.start();
		while (state != 1) {
			log.trace(
					"Waiting '-1' as confirmation from hsql engine, got '{}'.",
					state);
			Thread.yield();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// nothing special
			}
			state = server.getState();
		}
		log.info("Hsql engine started.");

		log.info("Starting databases post setup...");
		for (int i = 0; i < dbs.size(); i++) {
			final HsqlDatabase db = dbs.get(0);
			log.info("Post start up for db {}/{}.", db, dbs.size());
			db.postStartSetUp();
		}
		log.info("Post setup completed.");

		log.info("Dump connection strings:");
		for (HsqlDatabase db : dbs) {
			log.info("Connection to db {}: {}, account {}/{} ", db.getName(),
					db.getUrl(), db.getName(), db.getPassword());
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
	 * Returns the {@link HsqlDatabase} with the given name.
	 */
	@Override
	public HsqlDatabase dbByName(final String dbName) {
		Collection<HsqlDatabase> select = CollectionUtils.select(dbs,
				new Predicate<HsqlDatabase>() {

					@Override
					public boolean evaluate(HsqlDatabase db) {
						return dbName.equals(db.getName());
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
	DataSource getFastDataSource(HsqlDatabase hsqlDatabase) {
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(hsqlDatabase.getDriverClassName());
		bds.setUsername(hsqlDatabase.getUsername());
		bds.setPassword(hsqlDatabase.getPassword());
		bds.setUrl(hsqlDatabase.getUrl());
		return bds;
	}

	// ===============================================================
	// Properties
	// ===============================================================

	public String getHostName() {
		return "localhost";
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

	public boolean add(HsqlDatabase e) {
		currentStatus.onPropertyChange();
		e.setDbms(this);
		return dbs.add(e);
	}

	public void setDatabases(List<HsqlDatabase> dbs) {
		currentStatus.onPropertyChange();
		for (HsqlDatabase hsqlDatabase : dbs) {
			add(hsqlDatabase);
		}
	}

	void transitionTo(Status newStatus) {
		this.currentStatus = newStatus;
	}

}
