package com.danidemi.jlubricant.org.springframework.security.core;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

public class StringBasedGrantedAuthorityTest {

	@Test 
	public void shouldReturnAuthorities(){
		
		// when
		List<GrantedAuthority> authorities = StringBasedGrantedAuthority.toAuthorities("ADMIN_ROLE", "USER_ROLE");
		
		// then
		assertThat(authorities.get(0).getAuthority(), equalTo("ADMIN_ROLE"));
		assertThat(authorities.get(1).getAuthority(), equalTo("USER_ROLE"));
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullAuthorities(){
		
		List<GrantedAuthority> authorities = StringBasedGrantedAuthority.toAuthorities("ROLE1", null, "ROLE2");
				
	}

}
