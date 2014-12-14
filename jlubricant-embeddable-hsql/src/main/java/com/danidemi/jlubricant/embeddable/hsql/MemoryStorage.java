package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

public class MemoryStorage extends Storage {
	
	@Override
	public void register(HsqlDatabaseDescriptor hsqlDatabase, Registration registration) {
		registration.register(hsqlDatabase.getDbName(), "mem:" + hsqlDatabase.getDbName());
	}

}
