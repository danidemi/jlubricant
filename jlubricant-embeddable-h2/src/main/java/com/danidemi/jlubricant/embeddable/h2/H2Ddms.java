package com.danidemi.jlubricant.embeddable.h2;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.tools.Server;

import com.danidemi.jlubricant.embeddable.EmbeddableServer;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;

public class H2Ddms implements EmbeddableServer {

	private Server server;
	
	private File baseDir;

	private List<H2Database> dbs;
	
	
	
	public H2Ddms() {
		super();
		this.dbs = new ArrayList<>();
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	@Override
	public void start() throws ServerStartException {
		try {
			server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpDaemon", "-baseDir", baseDir.getAbsolutePath()).start();
		} catch (SQLException e) {
			throw new ServerStartException(e);
		}
		for (H2Database db : dbs) {
			db.postStart();
		}
	}

	@Override
	public void stop() throws ServerStopException {
		server.stop();
	}

	public void add(H2Database h2Database) {
		dbs.add(h2Database);
		
	}

}
