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
import org.hsqldb.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danidemi.jlubricant.embeddable.BasicDataSource;
import com.danidemi.jlubricant.embeddable.Dbms;
import com.danidemi.jlubricant.embeddable.EmbeddableServer;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;
import com.danidemi.jlubricant.slf4j.utils.LubricantLoggerWriter;
import com.danidemi.jlubricant.slf4j.utils.OneLogLineForEachFlush;
import com.danidemi.jlubricant.slf4j.utils.Replace;

import static org.apache.commons.collections4.CollectionUtils.*;

/**
 * An embeddable Dbms based on Hsql.
 * @author danidemi
 */
public class HsqlDbms implements EmbeddableServer, Dbms {
	
	private static Logger log = LoggerFactory.getLogger(HsqlDbms.class);
	private static Logger hsql = LoggerFactory.getLogger(HsqlDbms.class.getName() + ".hsql");

	
	static interface Registration {
		void register(String name, File path);
	}

        /** The Hsql server. */
	private Server server;
        
        /** The list of HSQL databases. */
	ArrayList<HsqlDatabase> dbs;
		private LubricantLoggerWriter lubricantLoggerWriter;

	public HsqlDbms() {
		dbs = new ArrayList<>(0);
		server = null;
	}
	
	

	public boolean add(HsqlDatabase e) {
		e.setDbms(this);
		return dbs.add(e);
	}
	
	public void setDatabases(List<HsqlDatabase> dbs) {
		for (HsqlDatabase hsqlDatabase : dbs) {
			add(hsqlDatabase);
		}
	}

	@Override
	public void start() throws ServerStartException {
		
		
		// ...evaluate whether a stand alone server is needed or not 
		final AtomicBoolean serverRequired = new AtomicBoolean(false);		
		forAllDo(dbs, new Closure<HsqlDatabase>() {
			@Override
			public void execute(HsqlDatabase input) {
				serverRequired.set( serverRequired.get() || input.requireStandaloneServer() );
			}
		});
		
		
		if(serverRequired.get()){
			
			log.info("Starting HSQL standalone DBMS...");
			
			server = new Server();
			lubricantLoggerWriter = new LubricantLoggerWriter( 
					hsql,
					new Replace(new OneLogLineForEachFlush(),  "\\[[\\w\\W]*\\]: ", "") ,
					com.danidemi.jlubricant.slf4j.Logger.TRACE 
			);
			server.setLogWriter( lubricantLoggerWriter.asPrintWriter() );
			server.setDaemon(true);
			server.setSilent(false);
			server.setTrace(false);
			server.setNoSystemExit(true);

			final AtomicInteger dbCounter = new AtomicInteger(0);
			for (HsqlDatabase db : dbs) {
				if(db.requireStandaloneServer()){
					
					db.register( new Registration(){

						@Override
						public void register(String name, File path) {
							
							log.info("Registering db #{} : '{}' stored on '{}'", dbCounter, name, path);
							HsqlDbms.register( server, dbCounter.getAndAdd(1), name, path );							
						} 
						
					} );
					
				}
			}

			log.info("Starting server.");
			int state = server.start();
			
			try{
				while (state != 1) {
					log.trace("Waiting for confirmation from server " + state + ".");
					Thread.yield();
					Thread.sleep(100);
					state = server.getState();
				}
			}catch(Exception e){
				throw new ServerStartException(e);
			}
			
			
		}

		log.info("Post setup in progress...");
		for (HsqlDatabase db : dbs) {
			db.postStartSetUp();
		}
		
		log.info("HSQL standalone DBMS is ready");			
		
	}
	


	private static void register(Server server2, int dbCounter, String name, File path) {
		server2.setDatabaseName(dbCounter, name);
		server2.setDatabasePath(dbCounter, path.getAbsolutePath());		
	}



	@Override
	public void stop() throws ServerStopException {
		if(server == null) return;
		server.shutdown();
		
		try{
			
			int state = server.getState();
			while (state != 16) {
				Thread.yield();
				Thread.sleep(100);
				state = server.getState();
			}
			
			lubricantLoggerWriter.close();
			
		}catch(Exception e){
			
			throw new ServerStopException(e);
			
		}


	}
	
	/** Returns a DataSource on the given database. */
	@Override
	public DataSource dataSourceByName(String dbName) {
		return new BasicDataSource( dbByName(dbName) );
	}

	@Override
	public HsqlDatabase dbByName(final String dbName) {
		Collection<HsqlDatabase> select = CollectionUtils.select(dbs, new Predicate<HsqlDatabase>() {

			@Override
			public boolean evaluate(HsqlDatabase db) {
				return dbName.equals( db.getName() );
			}

		});
		if(CollectionUtils.size(select)==0){
			throw new IllegalArgumentException("There are no databases called '" + dbName + "'");
		}
		if(CollectionUtils.size(select)>=2){
			throw new IllegalArgumentException("More than one database is called '" + dbName + "'");
		}
		return CollectionUtils.extractSingleton( select );
	}



	public String getHostName() {
		return "localhost";
	}

}
