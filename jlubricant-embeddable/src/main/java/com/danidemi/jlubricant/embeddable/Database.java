package com.danidemi.jlubricant.embeddable;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {

	Connection newConnection() throws SQLException;

}
