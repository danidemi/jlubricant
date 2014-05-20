package com.danidemi.jlubricant.embeddable.h2;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.h2.tools.Server;

import com.danidemi.jlubricant.embeddable.Database;
import com.danidemi.jlubricant.embeddable.Dbms;
import com.danidemi.jlubricant.embeddable.EmbeddableServer;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;


public class H2Ddms implements EmbeddableServer, Dbms {

	private Server server;
	
	private File baseDir;

	private List<H2DatabaseDescription> dbs;
	private List<H2DatabaseWorking> dbsw;
	
	public H2Ddms() {
		super();
		this.dbs = new ArrayList<>();
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public void start() throws ServerStartException {
		
		dbsw = new ArrayList<>();
		for (H2DatabaseDescription descriptor : dbs) {
			dbsw.add( new H2DatabaseWorking(descriptor, this) );
		}
		
		try {
			server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpDaemon", "-baseDir", baseDir.getAbsolutePath()).start();
		} catch (SQLException e) {
			throw new ServerStartException(e);
		}
		for (H2DatabaseWorking db : dbsw) {
			db.postStart();
		}
	}

	@Override
	public void stop() throws ServerStopException {
		server.stop();
	}

	public void add(H2DatabaseDescription h2Database) {
		dbs.add(h2Database);
	}

	@Override
	public Database dbByName(final String dbName) {
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
