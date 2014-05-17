package com.github.jlubricant.embeddable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.jlubricant.embeddable.HsqlDbms.Registration;

public class HsqlDatabase {

	private Storage storage;
	private Compatibility compatibility;
	private String dbName;
	private HsqlDbms dbms;
	

	public HsqlDatabase() {
		setCompatibility(new HsqlCompatibility());
	}
	
	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	
	public void setCompatibility(Compatibility compatibility) {
		this.compatibility = compatibility;
	}
	
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public String getName() {
		return dbName;
	}
	
	Connection newConnection() throws ClassNotFoundException, SQLException {


		Class.forName(getDriverName());
		String protocol = storage.getProtocol();
		String location = storage.getLocation( dbName, dbms );
		String jdbcUrl = "jdbc:hsqldb:" + protocol + ":" + location;
		
		Connection conn = DriverManager.getConnection(jdbcUrl, "sa", "");
		return conn;
	}

	public String getDriverName() {
		return "org.hsqldb.jdbcDriver";
	}

	void executeStm(String statement) {

		try (Connection conn = newConnection(); Statement stm = conn.createStatement()) {

			stm.execute(statement);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public boolean requireStandaloneServer() {
		return storage.requireStandaloneServer();
		
	}

	public void postStartSetUp() {
		compatibility.postStartSetUp(this);
		
	}

	public void register(Registration registration) {
		storage.register(this, registration);
		
	}

	void setDbms(HsqlDbms hsqlDbms) {
		if(dbms!=null){
			throw new IllegalStateException("Already registered to a dbms");
		}
		this.dbms = hsqlDbms;
	}
	
}
