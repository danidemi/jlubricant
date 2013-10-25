package com.github.jlubricant.test;

import java.sql.Connection;

public interface Db {

	Connection newConnection();

	public abstract String getConnectionString();

	public abstract String getDriverClassName();

	public abstract String getUsername();

	public abstract String getPassword();

}
