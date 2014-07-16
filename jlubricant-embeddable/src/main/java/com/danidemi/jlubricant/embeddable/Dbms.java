package com.danidemi.jlubricant.embeddable;

import javax.sql.DataSource;



public interface Dbms {

	Database dbByName(String dbName);

	DataSource dataSourceByName(String dbName);

}
