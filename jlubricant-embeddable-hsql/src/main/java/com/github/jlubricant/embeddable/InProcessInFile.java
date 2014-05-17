package com.github.jlubricant.embeddable;

import java.io.File;

public class InProcessInFile extends Storage {
	
	private File folder;

	@Override
	public boolean requireStandaloneServer() {
		return false;
	}

	@Override
	public String getProtocol() {
		return "file";
	}

	@Override
	public String getLocation(String dbName, HsqlDbms dbms) {
		return folder.getAbsolutePath();
	}

}
