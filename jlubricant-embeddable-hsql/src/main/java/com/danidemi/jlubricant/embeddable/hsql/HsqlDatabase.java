package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

public class HsqlDatabase {

	private Storage storage;
	private Compatibility compatibility;
	private String dbName;
	private HsqlDbms dbms;
	

	public HsqlDatabase() {
		setCompatibility(new HsqlCompatibility());
	}
	
	public HsqlDatabase(String name, Storage storage) {
		super();
		this.dbName = name;
		this.storage = storage;
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

	void setSyntax(String syntax) {
		executeStm("set database sql syntax "
				+ syntax
				+ " "
				+ "true");
	}

	void setTransactionControl() {
		executeStm("set database transaction control");
	}

	void setTransactionRollbackOnConflict(boolean setTransactionRollbackOnConflict) {
		executeStm("set database transaction rollback on conflict "
				+ setTransactionRollbackOnConflict);
	}

	void setDatabaseSqlConcatNulls(boolean setDatabaseSqlConcatNulls) {
		executeStm("set database sql concat nulls "
				+ setDatabaseSqlConcatNulls);
	}

	void setDatabaseSqlNullsFirst(boolean setDatabaseSqlNullsFirst) {
		executeStm("set database sql nulls first "
				+ setDatabaseSqlNullsFirst);
	}

	void setDatabaseSqlUniqueNulls(boolean setDatabaseSqlUniqueNulls) {
		executeStm("set database sql unique nulls "
				+ setDatabaseSqlUniqueNulls);
	}
	
}
