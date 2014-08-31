package com.danidemi.jlubricant.embeddable.hsql;

import com.danidemi.jlubricant.embeddable.hsql.HsqlDbms.Registration;

public abstract class Storage {

	/**
	 * @deprecated 
	 * If the storage requires the server to be started in standaolne mode. 
	 */
	@Deprecated
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
