package com.github.jlubricant.embeddable;

import java.io.File;

public class OracleCompatibility extends Compatibility {

	public OracleCompatibility() {
		super();
	}

	@Override
	public void postStartSetUp(HsqlDatabase hsqlDatabase) {
		hsqlDatabase.executeStm("set database sql syntax ORA true");
		hsqlDatabase.executeStm("set database transaction control");
		hsqlDatabase.executeStm("set database transaction rollback on conflict true");
		hsqlDatabase.executeStm("set database sql unique nulls true");
		hsqlDatabase.executeStm("set database sql nulls first false");
		hsqlDatabase.executeStm("set database sql concat nulls true");		
	}

}
