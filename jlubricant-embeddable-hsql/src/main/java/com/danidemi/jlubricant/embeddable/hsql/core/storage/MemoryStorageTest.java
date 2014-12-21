package com.danidemi.jlubricant.embeddable.hsql.core.storage;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.IntegerMapper;

import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms;
import com.danidemi.jlubricant.embeddable.hsql.core.compatibility.HsqlCompatibility;
import com.danidemi.jlubricant.embeddable.hsql.core.storage.MemoryStorage;

public class MemoryStorageTest extends MemoryStorage {

	@Test
	public void shouldWorkInMemory() throws Exception {
		
		HsqlDatabaseDescriptor memoryDb = new HsqlDatabaseDescriptor( "mem", new MemoryStorage(), new HsqlCompatibility(), "usr", "pwd" );
		
		HsqlDbms dbms = new HsqlDbms( memoryDb );		
		dbms.start();
		
		DBI dbi = new DBI(memoryDb);
		Handle h = dbi.open();
		h.execute("CREATE TABLE PEOPLE(NAME VARCHAR(64))");
		h.execute("INSERT INTO PEOPLE(NAME) VALUES('John')");
		int people = h.createQuery("SELECT COUNT(*) FROM PEOPLE").map(IntegerMapper.FIRST).first();
		h.close();
		
		assertThat( people, equalTo(1));
		
		dbms.stop();
		
	}

}
