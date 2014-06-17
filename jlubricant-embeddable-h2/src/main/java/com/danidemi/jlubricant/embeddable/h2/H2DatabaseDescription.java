package com.danidemi.jlubricant.embeddable.h2;

/** Describes an H2 database to be managed by an H2Dbms. */
public class H2DatabaseDescription {

	private String dbName;
	private H2Storage h2Storage;
	private String username;
	private String password;

        /** Describe an H2 in memory database with given name. */
	public H2DatabaseDescription(String dbName) {
		this.dbName = dbName;
		this.h2Storage = new MemoryStorage();
	}
	
        /** Describe an H2 database that will store data in the given storage. */
	public H2DatabaseDescription(String dbName, H2Storage storage) {
		this.dbName = dbName;
		this.h2Storage = storage;
	}
	
        /** Describe an H2 database that will store data in the given storage. */
	public H2DatabaseDescription(String dbName, H2Storage storage, String username, String password) {
		this.dbName = dbName;
		this.h2Storage = storage;
		this.username = username;
		this.password = password;
	}	
	
	public String getDbName() {
		return dbName;
	}
	
	String getUsername() {
		return username;
	}
	
	String getPassword() {
		return password;
	}

	public void accept(Visitor v) {
		v.visit(this);
		v.visit(h2Storage);
	}


	




}
