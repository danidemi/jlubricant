package com.github.jlubricant.embeddable;

public class InProcessInResource extends Storage {

	private String resourcePath;
	
	public InProcessInResource(String resourcePath) {
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
