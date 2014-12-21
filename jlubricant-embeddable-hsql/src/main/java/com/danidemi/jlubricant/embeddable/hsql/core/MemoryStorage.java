package com.danidemi.jlubricant.embeddable.hsql.core;

import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms.LocationConfiguration;

public class MemoryStorage extends Storage {
	
	@Override
	public void contributeToServerConfiguration(HsqlDatabaseDescriptor hsqlDatabase, LocationConfiguration registration) {
		registration.setLocation(hsqlDatabase.getDbName(), "mem:" + hsqlDatabase.getDbName());
	}

}
