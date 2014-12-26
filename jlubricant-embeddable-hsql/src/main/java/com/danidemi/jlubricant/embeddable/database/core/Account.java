package com.danidemi.jlubricant.embeddable.database.core;

/**
 * An account to a system with username and password.
 */
public interface Account {

	/** Get the password. */
	String getPassword();

	/** Get the username. */
	String getUsername();

}
