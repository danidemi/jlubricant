package com.danidemi.jlubricant.embeddable;

import com.danidemi.jlubricant.contracts.DatabaseContractTest;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlCompatibility;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms;
import com.danidemi.jlubricant.embeddable.hsql.core.MemoryStorage;

public class DatabaseContract extends DatabaseContractTest {

	private HsqlDbms dbms;
		
	@Override
	public void stopTest() throws ServerException {
		dbms.stop();
	}
		
	@Override
	protected JdbcDatabaseDescriptor buildADatabaseWithUsernameAndPassword(String username, String password) throws ServerException {

		HsqlDatabaseDescriptor db = new HsqlDatabaseDescriptor("adb", new MemoryStorage(), new HsqlCompatibility(), username, password);
		dbms = new HsqlDbms(db);
		dbms.start();
		return db;
	}

	@Override
	public void startTest() {
		// TODO Auto-generated method stub
		
	}



}
