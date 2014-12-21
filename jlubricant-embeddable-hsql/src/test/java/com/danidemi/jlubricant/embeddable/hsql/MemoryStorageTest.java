package com.danidemi.jlubricant.embeddable.hsql;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class MemoryStorageTest extends MemoryStorage {

	@Test
	public void shouldWorkInMemory() throws Exception {
		
		HsqlDatabaseDescriptor memoryDb = new HsqlDatabaseDescriptor( "mem", new MemoryStorage(), new HsqlCompatibility(), "usr", "pwd" );
		
		HsqlDbms dbms = new HsqlDbms( memoryDb );		
		dbms.start();
		
		memoryDb.executeStatement("CREATE TABLE PEOPLE(NAME VARCHAR(64))");
		memoryDb.executeStatement("INSERT INTO PEOPLE(NAME) VALUES('John')");
		
		Connection newConnection = memoryDb.getConnection();
		PreparedStatement prepareStatement = newConnection.prepareStatement("SELECT COUNT(*) FROM PEOPLE");
		ResultSet executeQuery = prepareStatement.executeQuery();
		executeQuery.next();
		assertThat( executeQuery.getInt(1), equalTo(1));
		
		dbms.stop();
		
	}

}
