package com.github.jlubricant.embeddable;

public class InProcessInMemory extends Storage {

	@Override
	public boolean requireStandaloneServer() {
		return false;
	}

	@Override
	public String getProtocol() {
		return "mem";
	}

	@Override
	public String getLocation(String dbName, HsqlDbms dbms) {
		return dbName;
	}

}
