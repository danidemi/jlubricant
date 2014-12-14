package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

public abstract class Storage {

	public abstract void register(HsqlDatabaseDescriptor hsqlDatabase, Registration registration);

}
