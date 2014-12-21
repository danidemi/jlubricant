package com.danidemi.jlubricant.embeddable.h2;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danidemi.jlubricant.embeddable.BasicDataSource;
import com.danidemi.jlubricant.embeddable.Dbms;
import com.danidemi.jlubricant.embeddable.EmbeddableServer;
import com.danidemi.jlubricant.embeddable.JdbcDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;

/** 
 * An H2 based embeddable {@link Dbms}.
 * @author danidemi
 */
public class H2Dbms implements EmbeddableServer, Dbms {
	
	private static Logger log = LoggerFactory.getLogger(H2Dbms.class);

	/** the H2 server. */
	private Server server;
	
	/** ??? */
	private File baseDir;

	/** List of databases we'll have to provide access to after startup. */
	private List<H2DatabaseDescription> dbs;
        
	/** Working databases. */
	private List<H2DatabaseWorking> dbsw;
	
	/** Set up a new {@link Dbms} based on H2 without any running database. */
	public H2Dbms() {
		super();
		this.dbs = new ArrayList<>();
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public void start() throws ServerStartException {
		
		//if(baseDir == null) throw new IllegalStateException("baseDir could not be null");
		
		dbsw = new ArrayList<>();
		boolean memoryOnly = true;
		for (H2DatabaseDescription descriptor : dbs) {
			memoryOnly = memoryOnly && descriptor.isMemoryMode();
			H2DatabaseWorking workingDb = new H2DatabaseWorking(descriptor, this);
			dbsw.add( workingDb );
			
			log.info("Adding DB {} {} {} {} {}", workingDb.getName(), workingDb.getDriverClassName(), workingDb.getUrl(), workingDb.getUsername(), workingDb.getPassword());
			
		}
		
		try {
			
			List<String> params = new ArrayList<String>();
			params.add( "-tcp" );
			params.add( "-tcpAllowOthers" );
			params.add( "-tcpDaemon" );
			if(!memoryOnly){
				params.add( "-baseDir" );
				params.add( baseDir.getAbsolutePath() );
			}
			
			
			
			server = Server.createTcpServer( params.toArray(new String[]{}) ).start();
			for (H2DatabaseWorking db : dbsw) {
				db.postStart();
			}
		} catch (SQLException e) {
			throw new ServerStartException(e);
		}
	}

	@Override
	public void stop() throws ServerStopException {
		server.stop();
	}

	/** Add a database. */
	public void add(H2DatabaseDescription h2Database) {
		dbs.add(h2Database);
	}
	
	@Override
	public DataSource dataSourceByName(String dbName) {
		return new BasicDataSource( dbByName(dbName) );
	}

	/** Get a database by its name. */
	@Override
	public JdbcDatabaseDescriptor dbByName(final String dbName) {
		Collection<H2DatabaseWorking> select = CollectionUtils.select(dbsw, new Predicate<H2DatabaseWorking>() {

			@Override
			public boolean evaluate(H2DatabaseWorking db) {
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

	public void accept(UrlVisitor v) {
		v.visit(this);
	}

	public String getProtocol() {
		return "tcp";		
	}

}
