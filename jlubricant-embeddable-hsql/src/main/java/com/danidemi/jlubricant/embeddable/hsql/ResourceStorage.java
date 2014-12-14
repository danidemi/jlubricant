package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

public class ResourceStorage extends Storage {

	private String resourcePath;
	
	public ResourceStorage(String resourcePath) {
		super();
		this.resourcePath = resourcePath;
	}

	@Override
	public void register(HsqlDatabaseDescriptor hsqlDatabase, Registration registration) {
		registration.register(hsqlDatabase.getDbName(), "res:" + resourcePath);
	}

}
