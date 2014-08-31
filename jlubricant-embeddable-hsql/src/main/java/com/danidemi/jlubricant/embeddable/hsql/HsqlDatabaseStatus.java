package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.Connection;
import java.sql.SQLException;

public interface HsqlDatabaseStatus {

	Connection getConnection() throws SQLException;

}
