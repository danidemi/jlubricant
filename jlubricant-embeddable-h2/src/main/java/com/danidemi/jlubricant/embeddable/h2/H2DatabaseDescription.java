package com.danidemi.jlubricant.embeddable.h2;



/** Describes an H2 database to be managed by an H2Dbms. */
public class H2DatabaseDescription {
	
	private static final String DEFAULT_USERNAME = "user";
	private static final String DEFAULT_PASSWORD = "pwd";

	private final String dbName;
	private final H2Storage h2Storage;
	private final String username;
	private final String password;

        /** Describe an H2 in memory database with given name. */
	public H2DatabaseDescription(String dbName) {
		this(dbName, new MemoryStorage(), DEFAULT_USERNAME, DEFAULT_PASSWORD);		
	}
	
        /** Describe an H2 database that will store data in the given storage. */
	public H2DatabaseDescription(String dbName, H2Storage storage) {
		this(dbName, storage, DEFAULT_USERNAME, DEFAULT_PASSWORD);
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
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}

	public void accept(Visitor v) {
		v.visit(this);
		v.visit(h2Storage);
	}

	public String getDriverClassName() {
		return "org.h2.Driver";
	}

	public boolean isMemoryMode() {
		return h2Storage.isMemoryMode();
	}
	
	

}
