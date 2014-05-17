package com.github.jlubricant.embeddable;

import com.github.jlubricant.embeddable.HsqlDbms.Registration;

public abstract class Storage {

	public abstract boolean requireStandaloneServer();

	public final void register(HsqlDatabase hsqlDatabase, Registration registration) {
		if(requireStandaloneServer()){
			doRegister(hsqlDatabase, registration);
		}
	}

	protected void doRegister(HsqlDatabase hsqlDatabase, Registration registration) {
		throw new UnsupportedOperationException("Please, override doRegister()");
	}

	public abstract String getProtocol();

	

	public abstract String getLocation(String dbName, HsqlDbms dbms);

}
