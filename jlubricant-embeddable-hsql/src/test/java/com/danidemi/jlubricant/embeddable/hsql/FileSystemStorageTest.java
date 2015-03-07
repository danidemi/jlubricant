package com.danidemi.jlubricant.embeddable.hsql;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.core.IsNot;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms.LocationConfiguration;
import com.danidemi.jlubricant.embeddable.hsql.core.compatibility.HsqlCompatibility;
import com.danidemi.jlubricant.embeddable.hsql.core.storage.FileSystemStorage;

@RunWith(MockitoJUnitRunner.class)
public class FileSystemStorageTest {

	HsqlDbms hsqlDbms;
	
	@Rule public TemporaryFolder tmp = new TemporaryFolder();
	
	File root;
	
	@Mock HsqlDatabaseDescriptor dbDescriptor;
	@Mock LocationConfiguration locationConfig;
	
	@Before
	public void setRootFolder() {
		root = tmp.getRoot();
	}
	
	@Test
	public void shouldComplainIfTheGivenPathIsAFile() throws IOException {

		// given
		
		// ...a descriptor for a db called 'the-y-db'
		when(dbDescriptor.getDbName()).thenReturn("the-y-db");
		
		// ...a 'dby' file, that we provide as folder
		File file = tmp.newFile();
				
		
		// when
		try{
			// ...a storage that uses the file
			FileSystemStorage storage = new FileSystemStorage( false, file );		
			fail(); 
		}catch(IllegalArgumentException iae){
			
			// then
			assertThat( iae.getMessage(), containsString(file.getAbsolutePath()) );
			
			
		}catch(Exception e){
			fail(); 
		}
		
	}
	
	@Test
	public void shouldNotCreateTheFolderIfToldNotToDoIt() throws IOException {
		
		// given
		
		// ...a descriptor for a db called 'the-x-db'
		when(dbDescriptor.getDbName()).thenReturn("the-x-db");
		
		// ...a 'test' folder that does not exist
		File dbFolder = new File( tmp.getRoot(), "test");
		
		assertThat( dbFolder.exists() , is(false));
		
		// ...a storage that uses that non existing folder, that should not create unexisting folders
		FileSystemStorage storage = new FileSystemStorage( false, dbFolder );
		
		// when
		try{
			storage.contributeToServerConfiguration(dbDescriptor, locationConfig);		
			fail(); 
		}catch(IllegalArgumentException iae){
			
			// then
			assertThat( iae.getMessage(), containsString(dbFolder.getAbsolutePath()) );
			
			
		}catch(Exception e){
			fail(); 
		}
			
	}
		
	@Test 
	public void shouldCreateTheDatabaseFolderIfItDoesNotExist() throws IOException {
		
		// given
		
		// ...a descriptor for a db called 'the-db'
		when(dbDescriptor.getDbName()).thenReturn("the-db");
		
		// ...a 'test' folder that does not exist
		File dbFolder = tmp.newFolder("test");
		
		// ...a storage that uses that non existing folder
		FileSystemStorage storage = new FileSystemStorage( dbFolder );
		
		// when
		storage.contributeToServerConfiguration(dbDescriptor, locationConfig);
		
		// then
		// ...the database directory should be created, so, it should be there
		assertTrue(  dbFolder.exists() );
		assertTrue(  dbFolder.isDirectory() );
		verify( locationConfig ).setLocation("the-db", "file:" + new File(dbFolder, "the-db").getAbsolutePath());
		
	}

	@Test
	public void shouldIntendTheFileAsFolder() throws ServerException {
		
		// given
		// ...a filesystem storage
		File dbFolder = new File( root, "mydb" );
		FileSystemStorage storage = new FileSystemStorage( dbFolder );
		
		// ...a database saved on filesystem in that directory
		HsqlDatabaseDescriptor hsql = new HsqlDatabaseDescriptor("test-db", storage, new HsqlCompatibility(), "uuzzeerr", "ppaazzww0rrdd");
		
		// ...a new HSQL dbms
		hsqlDbms = new HsqlDbms(hsql);
		
		
		
		// when
		hsqlDbms.start();
		
		
		
		// then
		// ...the database directory should be created
		assertTrue( dbFolder.isDirectory() );
		
		// ...database files should be placed there
		String[] list = dbFolder.list();
		assertThat( "no db files found", list, not(emptyArray()));
			
	}
	
	@After public void tearDownDb() throws ServerException {
		if(hsqlDbms != null) hsqlDbms.stop();
	}
	
}
