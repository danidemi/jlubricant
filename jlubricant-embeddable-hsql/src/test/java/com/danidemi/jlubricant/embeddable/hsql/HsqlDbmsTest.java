package com.danidemi.jlubricant.embeddable.hsql;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sql.DataSource;

import org.hsqldb.lib.HsqlList;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms;
import com.danidemi.jlubricant.embeddable.hsql.MemoryStorage;
import com.danidemi.jlubricant.embeddable.hsql.FileSystemStorage;

public class HsqlDbmsTest {

	public @Rule TemporaryFolder tmp = new TemporaryFolder();
	private HsqlDbms dbms;
		
	@After
	public void stopDb() throws ServerException{
		if(dbms!=null) dbms.stop();
	}
	
	@Test
	public void shouldNotSupportChangingThePredefinedSAUser() throws ServerException, SQLException {
		
		HsqlDatabaseDescriptor db = new HsqlDatabaseDescriptor("memdb", new MemoryStorage(), new HsqlCompatibility(), "SAA", "thePassword");
		
		dbms = new HsqlDbms( Arrays.asList(db) );
		
		dbms.start();

		db.executePublicStm("CREATE TABLE PEOPLE(NAME varchar(164))");
		db.executePublicStm("INSERT INTO PEOPLE(NAME)VALUES('John')");
				
	}
	
	@Test
	public void shouldSupportInProcessInMemoryDatabase() throws ServerException, SQLException {
				
		HsqlDatabaseDescriptor db = new HsqlDatabaseDescriptor("memdb2", new MemoryStorage(), new HsqlCompatibility(), "SAA", "thePassword");
			
		dbms = new HsqlDbms( Arrays.asList(db) );
		
		dbms.start();
		
		db.executePublicStm("CREATE TABLE PEOPLE(NAME varchar(164))");
		db.executePublicStm("INSERT INTO PEOPLE(NAME)VALUES('John')");
		
	}
		
	@Test
	public void test() throws ServerException, IOException, SQLException {
				
		HsqlDatabaseDescriptor db = new HsqlDatabaseDescriptor("oracle-like", new FileSystemStorage(tmp.newFolder("oracle-like")), new OracleCompatibility(), "SAA", "thePassword");		
		
			
		dbms = new HsqlDbms( Arrays.asList(db) );
		
		dbms.start();
		
		HsqlDatabaseDescriptor dbByName = dbms.dbByName("oracle-like");
		dbByName.executePublicStm("CREATE TABLE PEOPLE(NAME varchar(164))");
		dbByName.executePublicStm("INSERT INTO PEOPLE(NAME)VALUES('John')");
				
	}
	
	@Test
	public void shouldSupportTwoDatabases() throws ServerException, IOException, SQLException {

		HsqlDatabaseDescriptor db1 = new HsqlDatabaseDescriptor("oracle-like", new FileSystemStorage(tmp.newFolder("oracle-like")), new OracleCompatibility(), "SAA", "thePassword");		
		
		HsqlDatabaseDescriptor db2 = new HsqlDatabaseDescriptor("hsql-like", new MemoryStorage(), new HsqlCompatibility(), "SAA", "thePassword");
				
		dbms = new HsqlDbms( Arrays.asList(db1, db2) );
		
		dbms.start();
				
		DBI dbi = new DBI(db1);
		Handle h = dbi.open();
		h.execute("CREATE TABLE PEOPLE(NAME varchar(164))");
		h.execute("INSERT INTO PEOPLE(NAME)VALUES(?)", "Brian");
		h.close();
		h = dbi.open(db2);
		h.execute("CREATE TABLE PEOPLE(NAME varchar(164))");
		h.execute("INSERT INTO PEOPLE(NAME)VALUES(?)", "Brian");
		h.close();		
						
	}
	
	@Test
	public void shouldSupportMultipleDbs() throws IOException, ServerException, SQLException  {
		
		int maxDbs = 4;
		ArrayList<HsqlDatabaseDescriptor> dbs = new ArrayList<HsqlDatabaseDescriptor>();
		for(int i=0; i<maxDbs; i++){
			dbs.add(new HsqlDatabaseDescriptor("db" + i, new FileSystemStorage(tmp.newFolder("db" + i)), new HsqlCompatibility(), "usr", "pwd"));
		}
		dbms = new HsqlDbms( dbs );
		dbms.start();
		
		for(int i=0; i<maxDbs; i++){
			HsqlDatabaseDescriptor dbByName = dbms.dbByName("db" + i);
			dbByName.executePublicStm("CREATE TABLE PEOPLE(NAME varchar(164))");
			dbByName.executePublicStm("INSERT INTO PEOPLE(NAME)VALUES('John')");
		}		
				
	}
	
	@Test
	public void shouldSupportOracle() throws IOException, ServerException, SQLException  {
		
		HsqlDatabaseDescriptor oraLikeDb = new HsqlDatabaseDescriptor(
				"oracle-like-2", 
				new FileSystemStorage(tmp.newFolder("oracle-like")), 
				new HsqlCompatibility(), 
				"usr", "pwd");

		dbms = new HsqlDbms( oraLikeDb );
		dbms.start();
		
		HsqlDatabaseDescriptor dbByName = dbms.dbByName("oracle-like-2");
		dbByName.executePublicStm("CREATE TABLE PEOPLE(NAME varchar(164))");
		dbByName.executePublicStm("INSERT INTO PEOPLE(NAME)VALUES('John')");

	}

}
