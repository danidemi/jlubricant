package com.danidemi.jlubricant.embeddable.database.core;

import com.danidemi.jlubricant.utils.hoare.Arguments;

/**
 * Immutable account with username and password.
 */
public class BaseAccount implements Account {
	
	private String password;
	private String username;
	
	public BaseAccount(String username, String password) {
		super();
		Arguments.checkNotBlank(username);
		Arguments.checkNotNull(password);
		this.password = password;
		this.username = username;
	}
	
	@Override
	public String getUsername() {
		return username;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		return "[" + username + "/" + (password.length() == 0 ? "<blank>" : password) + "]";
	}
}