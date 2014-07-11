package com.danidemi.jlubricant.embeddable;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDatabase;
import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms;

public class DatabaseContract extends DatabaseContractTest {

	private HsqlDbms dbms;
	
	@Override
	public void startTest() {
		dbms = new HsqlDbms();
	}
	
	@Override
	public void stopTest() throws ServerStopException {
		dbms.stop();
	}
		
	@Override
	protected Database buildADatabaseWithUsernameAndPassword(String username, String password) throws ServerStartException {

		HsqlDatabase db = new HsqlDatabase();
		db.setUsername(username);
		db.setPassword(password);
		dbms.add( db );
		dbms.start();
		return db;
	}


}
