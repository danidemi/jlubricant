package com.danidemi.jlubricant.embeddable.h2;

public class UrlVisitor implements Visitor {

	private String name;
	private String storage;
	private String protocol;

	@Override
	public void visit(H2DatabaseDescription h2DatabaseDescription) {
		this.name = h2DatabaseDescription.getDbName();
	}

	@Override
	public void visit(H2Storage h2Storage) {
		this.storage = h2Storage.getStorageSpecifier();
	}

	public void visit(H2Ddms h2Ddms) {
		protocol = h2Ddms.getProtocol();
	}
	
	public String jdbcUrl() {
		return "jdbc:h2:"
				+ protocol
				+ "://localhost/"
				+ storage
				+ ":" + name;
	}

}
