package com.danidemi.jlubricant.embeddable;

import com.danidemi.jlubricant.contracts.DatabaseContractTest;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDatabase;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms;
import com.danidemi.jlubricant.embeddable.hsql.MemoryStorage;

public class DatabaseContract extends DatabaseContractTest {

	private HsqlDbms dbms;
	
	@Override
	public void startTest() {
		dbms = new HsqlDbms();
	}
	
	@Override
	public void stopTest() throws ServerException {
		dbms.stop();
	}
		
	@Override
	protected Database buildADatabaseWithUsernameAndPassword(String username, String password) throws ServerException {

		HsqlDatabase db = new HsqlDatabase("adb", new MemoryStorage());
		db.setUsername(username);
		db.setPassword(password);
		dbms.add( db );
		dbms.start();
		return db;
	}


}
