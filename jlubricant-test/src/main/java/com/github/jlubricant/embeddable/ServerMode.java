package com.github.jlubricant.embeddable;

import java.io.File;

import com.github.jlubricant.embeddable.HsqlDbms.Registration;

public class ServerMode extends Storage {

	private final File dbFolder;

	public ServerMode(File newFolder) {
		this.dbFolder = newFolder;
	}

	public String getAbsolutePath() {
		return dbFolder.getAbsolutePath();
	}
	
	@Override
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
