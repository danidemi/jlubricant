package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.LocationConfiguration;

public class ResourceStorage extends Storage {

	private String resourcePath;
	
	public ResourceStorage(String resourcePath) {
		super();
		this.resourcePath = resourcePath;
	}

	@Override
	public void contributeToServerConfiguration(HsqlDatabaseDescriptor hsqlDatabase, LocationConfiguration registration) {
		registration.setLocation(hsqlDatabase.getDbName(), "res:" + resourcePath);
	}

}
