package com.danidemi.jlubricant.embeddable.hsql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.danidemi.jlubricant.embeddable.ServerException;

public class InMemoryDatabaseTest {
	
	private HsqlDbms dbms;
	
	@After public void stopDb() throws ServerException {
		if(dbms!=null) dbms.stop();
	}

	@Test public void test() throws ServerException {
		
		dbms = new HsqlDbms(new HsqlDatabaseDescriptor("mydb", new MemoryStorage(), new HsqlCompatibility(), "usr", "pwd"));
		dbms.start();
		
	}
	
}
