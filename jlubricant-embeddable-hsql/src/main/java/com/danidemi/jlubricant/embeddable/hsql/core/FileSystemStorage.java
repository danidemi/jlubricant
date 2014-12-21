package com.danidemi.jlubricant.embeddable.hsql.core;

import java.io.File;

import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms.LocationConfiguration;

public class FileSystemStorage extends Storage {

	private File dbFolder;

	/** The folder where the database files are stored. */
	public FileSystemStorage(File newFolder) {
		setDbFolder(newFolder);
	}
	
	public FileSystemStorage() {
	
	}
	
	public void setDbFolder(File dbFolder) {
				
		this.dbFolder = dbFolder;
	}

	public String getAbsolutePath() {
		return dbFolder.getAbsolutePath();
	}
		
	@Override
	public void contributeToServerConfiguration(HsqlDatabaseDescriptor hsqlDatabase, LocationConfiguration registration) {
		registration.setLocation(hsqlDatabase.getDbName(), "file:" + dbFolder);
	}
	
}
