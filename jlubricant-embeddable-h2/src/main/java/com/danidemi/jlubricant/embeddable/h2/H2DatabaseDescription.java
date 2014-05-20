package com.danidemi.jlubricant.embeddable.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.danidemi.jlubricant.embeddable.ServerStartException;

public class H2DatabaseDescription {

	private String dbName;
	private H2Storage h2Storage;

	public H2DatabaseDescription(String dbName) {
		this.dbName = dbName;
		this.h2Storage = new MemoryStorage();
	}
	
	public H2DatabaseDescription(String dbName, H2Storage storage) {
		this.dbName = dbName;
		this.h2Storage = storage;
	}
	
	public String getDbName() {
		return dbName;
	}

	public void accept(Visitor v) {
		v.visit(this);
		v.visit(h2Storage);
	}


	




}
