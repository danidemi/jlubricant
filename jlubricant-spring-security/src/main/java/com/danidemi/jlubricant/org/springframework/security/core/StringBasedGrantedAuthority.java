package com.danidemi.jlubricant.org.springframework.security.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

/**
 * A {@link String} based {@link GrantedAuthority}.
 */
public class StringBasedGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = 3089037344892259228L;
	
	private String authority;
	
	public StringBasedGrantedAuthority(String authority) {
		super();
		if( StringUtils.isBlank(authority) ) throw new IllegalArgumentException("authority cannot be null");
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority;
	}
	
	public static List<GrantedAuthority> toAuthorities(String...stringAuthorities){
		ArrayList<GrantedAuthority> result = new ArrayList<>(stringAuthorities.length);
		for (String string : stringAuthorities) {
			result.add(new StringBasedGrantedAuthority(string));
		}
		return result;
	}
}
