package com.danidemi.jlubricant.embeddable.hsql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.danidemi.jlubricant.embeddable.ServerException;

public class InMemoryDatabaseTest {
	
	private HsqlDbms dbms;

	@Before public void setDb() {
		dbms = new HsqlDbms();		
	}
	
	@After public void stopDb() throws ServerException {
		dbms.stop();
	}

	@Test public void test() throws ServerException {
		
		dbms.add(new HsqlDatabase("mydb", new MemoryStorage()));
		dbms.start();
		
	}
	
}
