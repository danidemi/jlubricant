package com.danidemi.jlubricant.embeddable.hsql;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class InProcessInMemoryTest extends InProcessInMemory {

	@Test
	public void shouldWorkInMemory() throws Exception {
		
		HsqlDatabase memoryDb = new HsqlDatabase( "mem", new InProcessInMemory() );
		
		HsqlDbms dbms = new HsqlDbms();
		dbms.add(memoryDb);
		
		dbms.start();
		
		memoryDb.executeStm("CREATE TABLE PEOPLE(NAME VARCHAR(64))");
		memoryDb.executeStm("INSERT INTO PEOPLE(NAME) VALUES('John')");
		
		Connection newConnection = memoryDb.newConnection();
		PreparedStatement prepareStatement = newConnection.prepareStatement("SELECT COUNT(*) FROM PEOPLE");
		ResultSet executeQuery = prepareStatement.executeQuery();
		executeQuery.next();
		assertThat( executeQuery.getInt(1), equalTo(1));
		
		dbms.stop();
		
	}

}
