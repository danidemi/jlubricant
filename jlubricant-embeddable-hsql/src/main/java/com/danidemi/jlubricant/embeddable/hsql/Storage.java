package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.LocationConfiguration;

public abstract class Storage {

	public abstract void contributeToServerConfiguration(HsqlDatabaseDescriptor hsqlDatabase, LocationConfiguration registration);

}
