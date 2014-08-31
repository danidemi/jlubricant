package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

public abstract class Storage {

	public abstract void register(HsqlDatabase hsqlDatabase, Registration registration);

	public abstract String getProtocol();

	public abstract String getLocation(String dbName, HsqlDbms dbms);

}
