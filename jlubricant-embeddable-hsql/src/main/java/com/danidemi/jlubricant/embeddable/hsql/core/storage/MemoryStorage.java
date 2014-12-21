package com.danidemi.jlubricant.embeddable.hsql.core.storage;

import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDatabaseDescriptor;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms;
import com.danidemi.jlubricant.embeddable.hsql.core.Storage;
import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms.LocationConfiguration;

public class MemoryStorage extends Storage {
	
	@Override
	public void contributeToServerConfiguration(HsqlDatabaseDescriptor hsqlDatabase, LocationConfiguration registration) {
		registration.setLocation(hsqlDatabase.getDbName(), "mem:" + hsqlDatabase.getDbName());
	}

}
