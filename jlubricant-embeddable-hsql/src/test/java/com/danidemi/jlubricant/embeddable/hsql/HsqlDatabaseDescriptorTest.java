package com.danidemi.jlubricant.embeddable.hsql;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HsqlDatabaseDescriptorTest {
	
	@Mock Storage storage;
	@Mock Compatibility compatibility;
	
	@Test
	public void shouldDescribeItself(){
		
		// given
		HsqlDatabaseDescriptor hsqlDatabaseDescriptor = new HsqlDatabaseDescriptor("dbName", storage, compatibility, "usr", "pwd");
		
		// when
		String toString = hsqlDatabaseDescriptor.toString();
		
		// then
		assertThat( toString, containsString("dbName") );

	}

	@Test
	public void shouldNotComplainIfArgumentsOk(){
		new HsqlDatabaseDescriptor("dbName", storage, compatibility, "usr", "pwd");
	}
	
	@Test
	public void shouldComplainIfArgumentsKo(){
		
		try{
			new HsqlDatabaseDescriptor(null, storage, compatibility, "usr", "pwd");
			fail();
		}catch(IllegalArgumentException iae){
			
		}
		
		try{
			new HsqlDatabaseDescriptor("dbName", null, compatibility, "usr", "pwd");
			fail();
		}catch(IllegalArgumentException iae){
			
		}
		
		try{
			new HsqlDatabaseDescriptor("dbName", storage, null, "usr", "pwd");
			fail();
		}catch(IllegalArgumentException iae){
			
		}
		
		try{
			new HsqlDatabaseDescriptor("dbName", storage, compatibility, null, "pwd");
			fail();
		}catch(IllegalArgumentException iae){
			
		}
		
		try{
			new HsqlDatabaseDescriptor("dbName", storage, compatibility, "usr", null);
			fail();
		}catch(IllegalArgumentException iae){
			
		}
		
	}	
	
	@Test(expected=IllegalStateException.class)
	public void shouldComplainIfAskingForConnectionWhenNotYetStarted() throws SQLException {
		
		HsqlDatabaseDescriptor hsqlDatabase = new HsqlDatabaseDescriptor("dbName", storage, compatibility, "usr", "pwd");
		hsqlDatabase.getConnection();
		
	}
	
}
