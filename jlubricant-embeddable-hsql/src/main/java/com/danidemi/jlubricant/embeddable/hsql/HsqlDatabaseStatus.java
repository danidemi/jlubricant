package com.danidemi.jlubricant.embeddable.hsql;

import java.sql.Connection;
import java.sql.SQLException;

abstract class HsqlDatabaseStatus {

	abstract Connection getConnection() throws SQLException;

}
