package com.danidemi.jlubricant.embeddable.hsql;

public class ResourceStorage extends Storage {

	private String resourcePath;
	
	public ResourceStorage(String resourcePath) {
		super();
		this.resourcePath = resourcePath;
	}

	@Override
	public boolean requireStandaloneServer() {
		return false;
	}

	@Override
	public String getProtocol() {
		return "res";
	}

	@Override
	public String getLocation(String dbName, HsqlDbms dbms) {
		return resourcePath;
	}

}
