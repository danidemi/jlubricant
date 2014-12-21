package com.danidemi.jlubricant.embeddable.hsql.core;

import com.danidemi.jlubricant.embeddable.hsql.core.HsqlDbms.LocationConfiguration;

public abstract class Storage {

	public abstract void contributeToServerConfiguration(HsqlDatabaseDescriptor hsqlDatabase, LocationConfiguration registration);

}
