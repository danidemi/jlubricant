package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.SQLException;

import org.junit.Test;


public class HsqlDatabaseTest {

	@Test(expected=IllegalStateException.class)
	public void shouldComplainIfAskingForConnectionWhenNotYetStarted() throws SQLException {
		
		HsqlDatabase hsqlDatabase = new HsqlDatabase();
		hsqlDatabase.getConnection();
		
	}
	
}
