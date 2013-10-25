package com.github.jlubricant.test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.hsqldb.Database;
import org.hsqldb.server.Server;

/**
 * This is a standalone DB that can be controlled through its methods, making it an ideal solution for
 * automatic tests that involve a db.
 * TestDb is backed by HSQLDB (http://hsqldb.org/).
 */
public final class HsqlDb implements Db {
	
	private static final String DB_PASSWORD = "";
	private static final String DB_USERNAME = "SA";
	private static final String JDBC_DRIVER = "org.hsqldb.jdbc.JDBCDriver";
		
	private final String dbName;
	private final String jdbcUrl;
	private final File hsqlFile;
	private Server s;
	private ArrayList<String> list;
	
	/**
	 * Creates a new {@link HsqlDb} with a name equals to the provided class'name.
	 */
	public HsqlDb(Class clazz){
		this(clazz.getSimpleName());
	}
	
	/**
	 * Creates a new {@link HsqlDb} with the provided name.
	 * The provided <code>dbName</code> will be used to define a folder in the
	 * local OS temporary directory where to store all db files.
	 */
	public HsqlDb(String dbName){
		if(dbName==null || dbName.trim().isEmpty()){
			throw new IllegalArgumentException("Please provide a not blank name.");
		}
		this.dbName = dbName;
		this.hsqlFile = new File(new File( FileUtils.getTempDirectory(), dbName ), dbName);
		this.jdbcUrl = "jdbc:hsqldb:hsql://localhost/" + dbName;
	}
	
	/**
	 * Creates a new {@link HsqlDb} with the provided name.
	 * The provided <code>dbName</code> stored in the given folder.
	 */
	public HsqlDb(String dbName, File dbFolder){
		if(dbName==null || dbName.trim().isEmpty()){
			throw new IllegalArgumentException("Please provide a not blank name.");
		}
		this.dbName = dbName;
		this.hsqlFile = new File(dbFolder, dbName);
		this.jdbcUrl = "jdbc:hsqldb:hsql://localhost/" + dbName;
	}
	
	/** Sinchronously start the db. */
	public void start(){
		
		if(s!=null && s.getState() == Database.DATABASE_ONLINE){
			return;
		}
		
		if(s == null){
			s = new Server();
		}
		
		s.setDatabaseName(0, dbName);
		s.setDatabasePath(0, "file:" + hsqlFile);
		s.setSilent(true);
				
		s.start();
		boolean isRunning = false;
		while(!isRunning){
			try {
				s.checkRunning(true);
				isRunning = true;
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
			}
		}
		
		int state = s.getState();
		while(state != 1){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
			}
		}
		
		
		
		while(!isSelfConnectionWorking()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
			}
		}
						
	}

	/** Sinchronously stop the db. */
	public void stop(){
		if(s==null) {
			return;
		}
		
		try{
			s.checkRunning(true);
		}catch (RuntimeException e) {
			// server is already stopped.
			s = null;
			return;
		}
		
		s.stop();
		boolean isStopped = false;
		int tries = 3;
		while(!isStopped && tries-- >= 0){
			try{
				s.checkRunning(false);
				isStopped = true;
				Thread.sleep(100);
			}catch (RuntimeException e) {
				// swallowing
			} catch (InterruptedException e) {
				// swallowing
			}
		}
				
		tries = 3;
		while(tries-->0 && isSelfConnectionWorking()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
			}
		}
		
		s = null;
	}

	/**
	 * Removes all the files of the DB.
	 * This means that if <code>truncate()</code> is executed, all the data previously
	 * existing will be lost.
	 */
	public void truncate() {
		List<File> roots = Arrays.asList( File.listRoots() );
		File parentFile = hsqlFile.getParentFile();
		if(parentFile.exists() && !roots.contains(parentFile)){
			try {
				FileUtils.cleanDirectory(parentFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/** The password to be used to connect to the database. */
	public String getPassword(){
		return DB_PASSWORD;
	}

	/** The username to be used to connect to the database. */
	public String getUsername(){
		return DB_USERNAME;
	}
	
	/** The name of the JDBC driver to be used to connect to the database. */
	public String getDriverClassName(){
		return JDBC_DRIVER;
	}
	
	/** The JDBC URL to be used to connect to the database. */
	public String getConnectionString(){
		return jdbcUrl;
	}
	
	/**
	 * Shorthand method to obtain a {@link Connection} to this DB.
	 * <strong>Just remember to close it!</strong>
	 */
	public Connection newConnection() {
		
		if(s==null || s.getState() != Database.DATABASE_ONLINE){
			throw new IllegalStateException("Cannot return a connection id the DB is not started.");
		}
		
		try {
			Class.forName(getDriverClassName());
			Connection connection = DriverManager.getConnection(getConnectionString());
			return connection;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Execute the given SQL. */
	public void execute(String sql) throws Exception{
		list = new ArrayList<String>();
		list.add(sql);
		this.executeBatch(list);
	}
	
	/** Execute the given SQL lines. */
	public void executeBatch(String... sqls) throws Exception{
		List<String> asList = Arrays.asList(sqls);
		executeBatch(asList);
	}
	
	/** Executes the list of provided queries. 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * */
	public void executeBatch(List<String> lines) throws ClassNotFoundException, SQLException {
	
		Class.forName(JDBC_DRIVER);
		Connection jdbcConnection = DriverManager.getConnection(jdbcUrl, DB_USERNAME, DB_PASSWORD);
		Statement stm = jdbcConnection.createStatement();
		for (String line : lines) {
			stm.addBatch(line);
		}
		stm.executeBatch();
		stm.close();
	
	}
	
	public int executeSingleLongResult(String sql) throws SQLException {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try{
			conn = newConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			if(rs.next()){
				return rs.getInt(1);
			}else{
				throw new RuntimeException("The result set did not provided any result.");
			}
		}finally{
			try{if(rs!=null) {
				rs.close();
			}}catch(SQLException e){}
			try{if(stat!=null) {
				stat.close();
			}}catch(SQLException e){}
			try{if(conn!=null) {
				conn.close();
			}}catch(SQLException e){}
		}
	}
	
	/**
	 * Returns the result of a SELECT for quick ispection as a {@link List} of {@link Map}.
	 * The list contains the records in the same order they are extracted.
	 * The map contains all fields of a given record along with their values.
	 */
	public List<Map<String, Object>> executeSelect(String sql) throws SQLException{
		
		ArrayList<Map<String, Object>> records = new ArrayList<Map<String, Object>>(0);
		
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try{
			conn = newConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			ResultSetMetaData metaData = rs.getMetaData();
			while(rs.next()){
				HashMap<String, Object> record = new HashMap<String, Object>();
				for(int i=1; i<= metaData.getColumnCount(); i++){
					record.put(metaData.getColumnName(i), rs.getObject(i));
				}
				records.add( record );
			}
			return records;
		}finally{
			try{if(rs!=null) {
				rs.close();
			}}catch(SQLException e){}
			try{if(stat!=null) {
				stat.close();
			}}catch(SQLException e){}
			try{if(conn!=null) {
				conn.close();
			}}catch(SQLException e){}
		}
	}

	/** Check if it is able to open a JDBC connection to itself. */
	private boolean isSelfConnectionWorking(){
		boolean result = true;
		try {
			Class.forName(JDBC_DRIVER);
			Connection jdbcConnection = DriverManager.getConnection(jdbcUrl, DB_USERNAME, DB_PASSWORD);
			jdbcConnection.close();
			
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
		
	/**
	 * A quick example of how to use the {@link HsqlDb}.
	 */
	public static void main(String[] args) {
		
		HsqlDb testDb = new HsqlDb("thedb");
		testDb.truncate();
		testDb.start();
		
		try {
			testDb.execute("CREATE TABLE PEOPLE(FIRST_NAME VARCHAR(64), LAST_NAME VARCHAR(64));");
			testDb.executeBatch(
					"INSERT INTO PEOPLE VALUES ('John', 'Smith')",
					"INSERT INTO PEOPLE VALUES ('Jean', 'Doe')",
					"INSERT INTO PEOPLE VALUES ('Rolando', 'Mc Click')",
					"INSERT INTO PEOPLE VALUES ('James', 'Bond')"
			);
			
			Class.forName(testDb.getDriverClassName());
			Connection connection = DriverManager.getConnection(testDb.getConnectionString());
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM PEOPLE");
			while(rs.next()){
				System.out.println( String.format("My name is %1$s... %2$s %1$s.", rs.getString("LAST_NAME"), rs.getString("FIRST_NAME")));
			}
			rs.close();
			statement.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		testDb.stop();
		
	}

}
