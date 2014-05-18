package com.danidemi.jlubricant.embeddable.hsql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.hsqldb.Server;

import com.danidemi.jlubricant.embeddable.EmbeddableServer;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;

import static org.apache.commons.collections4.CollectionUtils.*;

public class HsqlDbms implements EmbeddableServer {
	
	static interface Registration {
		void register(String name, File path);
	}

	Server server;
	ArrayList<HsqlDatabase> dbs;

	public HsqlDbms() {
		dbs = new ArrayList<>(0);
		server = null;
	}
	
	

	public boolean add(HsqlDatabase e) {
		e.setDbms(this);
		return dbs.add(e);
	}



	@Override
	public void start() throws ServerStartException {
		
		final AtomicBoolean serverRequired = new AtomicBoolean(false);		
		forAllDo(dbs, new Closure<HsqlDatabase>() {
			@Override
			public void execute(HsqlDatabase input) {
				serverRequired.set( serverRequired.get() || input.requireStandaloneServer() );
			}
		});
		
		
		if(serverRequired.get()){
			
			server = new Server();
			server.setDaemon(true);
			server.setSilent(false);
			server.setTrace(true);
			server.setNoSystemExit(true);

			final AtomicInteger dbCounter = new AtomicInteger(0);
			for (HsqlDatabase db : dbs) {
				if(db.requireStandaloneServer()){
					
					db.register( new Registration(){

						@Override
						public void register(String name, File path) {
							HsqlDbms.register( server, dbCounter.getAndAdd(1), name, path );
							
						} 
						
					} );
					
				}
			}

			server.start();
			
			for (HsqlDatabase db : dbs) {
				db.postStartSetUp();
			}
		}
		
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
			
		}catch(Exception e){
			
			throw new ServerStopException(e);
			
		}


	}

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
