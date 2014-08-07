package com.danidemi.jlubricant.embeddable.hsql;

import java.io.File;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

public class ServerMode extends Storage {

	private File dbFolder;

	/** The folder where the database files are stored. */
	public ServerMode(File newFolder) {
		this.dbFolder = newFolder;
	}
	
	public ServerMode() {
	
	}
	
	public void setDbFolder(File dbFolder) {
		this.dbFolder = dbFolder;
	}

	public String getAbsolutePath() {
		return dbFolder.getAbsolutePath();
	}
	
	@Override
	/** Whether this storage requires a standalone server. */
	public boolean requireStandaloneServer() {
		return true;
	}	
	
	@Override
	protected void doRegister(HsqlDatabase hsqlDatabase, Registration registration) {
		registration.register(hsqlDatabase.getName(), dbFolder);
	}

	@Override
	public String getProtocol() {
		return "hsql";
	}

	@Override
	public String getLocation(String dbName, HsqlDbms dbms) {
		return "//" + dbms.getHostName() + "/" + dbName;
	}


	
}
