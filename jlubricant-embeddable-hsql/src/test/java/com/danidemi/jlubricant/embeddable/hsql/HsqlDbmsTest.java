package com.danidemi.jlubricant.embeddable.hsql;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDatabase;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms;
import com.danidemi.jlubricant.embeddable.hsql.MemoryStorage;
import com.danidemi.jlubricant.embeddable.hsql.OracleCompatibility;
import com.danidemi.jlubricant.embeddable.hsql.FileSystemStorage;

public class HsqlDbmsTest {

	public @Rule TemporaryFolder tmp = new TemporaryFolder();
	private HsqlDbms dbms;
	
	@Before
	public void createDb(){
		
		dbms = new HsqlDbms();
		
	}
	
	@After
	public void stopDb() throws ServerException{
			
		dbms.stop();
		
	}
	
	@Test
	public void shouldNotSupportChangingThePredefinedSAUser() throws ServerException, SQLException {
		
		HsqlDatabase db;
		db = new HsqlDatabase();
		db.setDbName("memdb");
		db.setStorage(new MemoryStorage());
		db.setUsername("SA");
		db.setPassword("thePassword");
		
		dbms.add(db);
		
		dbms.start();

		db.executeStm("CREATE TABLE PEOPLE(NAME varchar(164))");
		db.executeStm("INSERT INTO PEOPLE(NAME)VALUES('John')");
				
	}
	
	@Test
	public void shouldSupportInProcessInMemoryDatabase() throws ServerException, SQLException {
				
		HsqlDatabase db = new HsqlDatabase();
		db.setDbName("memdb2");
		db.setStorage(new MemoryStorage());		
		
		dbms.add(db);
		
		dbms.start();
		
		db.executeStm("CREATE TABLE PEOPLE(NAME varchar(164))");
		db.executeStm("INSERT INTO PEOPLE(NAME)VALUES('John')");
		
	}
		
	@Test
	public void test() throws ServerException, IOException, SQLException {
				
		HsqlDatabase db = new HsqlDatabase();
		db.setDbName("oracle-like");
		db.setCompatibility( new OracleCompatibility() );
		db.setStorage(new FileSystemStorage(tmp.newFolder("oracle-like")));
		
			
		dbms.add(db);
		
		dbms.start();
		
		HsqlDatabase dbByName = dbms.dbByName("oracle-like");
		dbByName.executeStm("CREATE TABLE PEOPLE(NAME varchar(164))");
		dbByName.executeStm("INSERT INTO PEOPLE(NAME)VALUES('John')");
				
	}	
	
	@Test
	public void shouldSupportMultipleDbs() throws IOException, ServerException, SQLException  {
		
		int maxDbs = 4;
		for(int i=0; i<maxDbs; i++){
			HsqlDatabase db = new HsqlDatabase();
			db.setDbName("db" + i);
			db.setStorage(new FileSystemStorage(tmp.newFolder("db" + i)));			
			dbms.add(db);			
		}
		dbms.start();
		
		for(int i=0; i<maxDbs; i++){
			HsqlDatabase dbByName = dbms.dbByName("db" + i);
			dbByName.executeStm("CREATE TABLE PEOPLE(NAME varchar(164))");
			dbByName.executeStm("INSERT INTO PEOPLE(NAME)VALUES('John')");
		}		
				
	}
	
	@Test
	public void shouldSupportOracle() throws IOException, ServerException, SQLException  {
		
		HsqlDatabase oraLikeDb = new HsqlDatabase();
		oraLikeDb.setDbName("oracle-like-2");
		oraLikeDb.setStorage(new FileSystemStorage(tmp.newFolder("oracle-like")));

		dbms.add(oraLikeDb);
		dbms.start();
		
		HsqlDatabase dbByName = dbms.dbByName("oracle-like-2");
		dbByName.executeStm("CREATE TABLE PEOPLE(NAME varchar(164))");
		dbByName.executeStm("INSERT INTO PEOPLE(NAME)VALUES('John')");

	}

}
