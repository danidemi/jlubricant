package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.SQLException;

public interface PostStartContribution {

	void apply(HsqlDatabaseDescriptor hsqlDatabase) throws SQLException;

}
