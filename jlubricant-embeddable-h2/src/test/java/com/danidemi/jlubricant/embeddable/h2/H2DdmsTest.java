package com.danidemi.jlubricant.embeddable.h2;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ServerStartException;
import com.danidemi.jlubricant.embeddable.ServerStopException;

public class H2DdmsTest {
	
	public @Rule TemporaryFolder tmp = new TemporaryFolder(); 

	@Test
	public void shouldStartAndStop() throws ServerException, IOException {
		H2Ddms h2Ddms = new H2Ddms();
		h2Ddms.setBaseDir(tmp.newFolder());
		
		h2Ddms.start();
		h2Ddms.stop();
	}
	
	@Test
	public void shouldAddADatabaseToTheDbms() throws Exception {
		
		H2Ddms tested = new H2Ddms();
		tested.setBaseDir(tmp.newFolder());
		
		H2DatabaseDescription db = new H2DatabaseDescription("test");
		tested.add( db );
		
		tested.start();
		
		Connection conn = db.newConnection();
		
		Statement stm = conn.createStatement();
		stm.execute("CREATE TABLE PEOPLE(NAME CHAR(64))");
		stm.execute("INSERT INTO PEOPLE(NAME) VALUES('John')");
		ResultSet executeQuery = stm.executeQuery("SELECT COUNT(*) AS C FROM PEOPLE");
		executeQuery.next();
		int int1 = executeQuery.getInt(1);
		
		conn.close();

		conn = db.newConnection();
		
		stm = conn.createStatement();
		executeQuery = stm.executeQuery("SELECT COUNT(*) AS C FROM PEOPLE");
		executeQuery.next();
		int int2 = executeQuery.getInt(1);
				
		conn.close();
		
		assertEquals(int1, int2);
		
		tested.stop();
		
	}

}
