package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

public class MemoryStorage extends Storage {

	@Override
	public String getProtocol() {
		return "mem";
	}

	@Override
	public String getLocation(String dbName, HsqlDbms dbms) {
		return dbName;
	}
	
	@Override
	public void register(HsqlDatabase hsqlDatabase, Registration registration) {
		registration.register(hsqlDatabase.getName(), "mem:" + hsqlDatabase.getName());
	}

}
