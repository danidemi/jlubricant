package com.danidemi.jlubricant.embeddable.h2;

import java.util.HashMap;
import java.util.Map;

public class UrlVisitor implements Visitor {

	private String name;
	private String storage;
	private String protocol;
	private Map<String, String> params;
	
	public UrlVisitor() {
		params = new HashMap<String, String>();
	}

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
		String url = "jdbc:h2:"
				+ protocol
				+ "://localhost/"
				+ storage
				+ ":" + name;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			url = url + ";" + entry.getKey() + "=" + entry.getValue();
		}
		return url;
	}

	public UrlVisitor withParam(String string, String string2) {
		this.params.put(string, string2);
		return this;
	}

}
