package com.danidemi.jlubricant.embeddable;

import javax.sql.DataSource;



/**
 * Implementations describe a database through its usual JDBC parameters.
 */
public interface JdbcDatabaseDescriptor extends DataSource {

	String getUrl();

	String getPassword();

	String getDriverClassName();

	String getUsername();


}
