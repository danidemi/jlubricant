package com.danidemi.jlubricant.embeddable.hsql;

public class Db2Compatibility extends Compatibility {

	@Override
	public void apply(HsqlDatabase hsqlDatabase) {
		hsqlDatabase.setSyntax("DB2");
		hsqlDatabase.setTransactionControl();
	}

}
