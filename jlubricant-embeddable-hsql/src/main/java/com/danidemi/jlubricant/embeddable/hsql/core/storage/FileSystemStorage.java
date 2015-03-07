package com.danidemi.jlubricant.embeddable.hsql.core.storage;

import static java.lang.String.format;

import java.io.File;

import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms;
import com.danidemi.jlubricant.embeddable.hsql.core.Storage;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms.LocationConfiguration;

/**
 * A {@link Storage} that tells HSQL to keep database files in a given directory.
 */
public class FileSystemStorage extends Storage {

	private File dbFolder;
	private boolean createFolderIfMissing;

	/** 
	 * This {@link FileSystemStorage} will instruct {@link HsqlDbms} to keep all the info regarding a {@link HsqlDatabaseDescriptor}
	 * inside a given folder in the filesystem.
	 * @param The folder where the database files are stored. 
	 */
	public FileSystemStorage(File dbFolder) {
		this(true, dbFolder);
	}
	
	public FileSystemStorage() {
	
	}
	
	public FileSystemStorage(boolean createFolderIfMissing, File dbFolder) {
		this.createFolderIfMissing = createFolderIfMissing;
		setDbFolder(dbFolder);
	}

	public void setDbFolder(File dbFolder) {
		if(dbFolder.exists() && dbFolder.isFile()){
			throw new IllegalArgumentException(dbFolder + " is a file but should be a directory");
		}
		this.dbFolder = dbFolder;
	}

	public String getAbsolutePath() {
		return dbFolder.getAbsolutePath();
	}
		
	@Override
	public void contributeToServerConfiguration(HsqlDatabaseDescriptor hsqlDatabase, LocationConfiguration registration) {
		
		if(!dbFolder.exists()){
			
			if(!createFolderIfMissing) throw new IllegalArgumentException( 
					format("Folder '%s' does not exist, and it won't be automatically created. Please provide an existing folder.", dbFolder.getAbsolutePath()) );
			
			dbFolder.mkdirs();
		}
		File dbFile = new File(dbFolder, hsqlDatabase.getDbName());
		
		registration.setLocation(hsqlDatabase.getDbName(), "file:" + dbFile);
	}
	
}
