package com.danidemi.jlubricant.embeddable.hsql;

import java.io.File;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

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
	public void register(HsqlDatabaseDescriptor hsqlDatabase, Registration registration) {
		registration.register(hsqlDatabase.getDbName(), "file:" + dbFolder);
	}
	
}
