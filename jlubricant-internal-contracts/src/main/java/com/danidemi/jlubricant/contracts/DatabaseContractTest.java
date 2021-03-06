package com.danidemi.jlubricant.contracts;

import com.danidemi.jlubricant.embeddable.JdbcDatabaseDescriptor;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import org.junit.Test;


public abstract class DatabaseContractTest {
	
	public abstract void startTest();
	
	public abstract void stopTest() throws Exception;

	@Test public void shouldConnectWithSetUsernameAndPassword() throws Exception {
		
		
		String[][] usersAndPasswords = {
				{"username", "password"},
				{"THEUSER", "THEPASSWORD"},
				{"usr", ""}
		};
		
		for (String[] strings : usersAndPasswords) {
			
			startTest();
			
			String expectedUsername = strings[0];
			String expectedPassword = strings[1];
			
			// given
			JdbcDatabaseDescriptor db = null;
			try {
				db = buildADatabaseWithUsernameAndPassword(expectedUsername, expectedPassword);
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
			assertNotNull("Database cannot be null", db );
			
			// then			
			try {
				openANewConnectionAndThenClose(db);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
			stopTest();
			
		}
		
		
	}

	private void openANewConnectionAndThenClose(JdbcDatabaseDescriptor db) throws Exception {
		String className;
		String url;
		String username;
		String password;
		Connection connection;
		
		className = db.getDriverClassName();
		url = db.getUrl();
		username = db.getUsername();
		password = db.getPassword();
		Class.forName( className );
		connection = DriverManager.getConnection(url, username, password);
		connection.close();

	}

	protected abstract JdbcDatabaseDescriptor buildADatabaseWithUsernameAndPassword(String username, String password) throws Exception;
	
}
