package com.danidemi.jlubricant.org.springframework.security.provisioning;

import java.security.SecureRandom;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * An type of {@link org.springframework.security.provisioning.JdbcUserDetailsManager} that supports 
 * password encoding.
 */
public class JdbcUserDetailsManager extends org.springframework.security.provisioning.JdbcUserDetailsManager {

	private PasswordEncoder passwordEncoder;
	
	@Override
	public void createUser(UserDetails user) {
		super.createUser( new PasswordEncodedUser( user ) );
	}
	
	@Override
	public void updateUser(UserDetails user) {
		super.createUser( new PasswordEncodedUser( user ) );
	}
	
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
	private class PasswordEncodedUser implements UserDetails {
		
		private static final long serialVersionUID = -8103487155453483943L;
		private UserDetails delegate;
				
		private PasswordEncodedUser(UserDetails originalUser){
			this.delegate = originalUser;
		}

		public Collection<? extends GrantedAuthority> getAuthorities() {
			return delegate.getAuthorities();
		}

		public String getPassword() {
			String originalPassword = delegate.getPassword();
			String encodedPassword = JdbcUserDetailsManager.this.passwordEncoder.encode(originalPassword);
			return encodedPassword;
		}

		public String getUsername() {
			return delegate.getUsername();
		}

		public boolean isAccountNonExpired() {
			return delegate.isAccountNonExpired();
		}

		public boolean isAccountNonLocked() {
			return delegate.isAccountNonLocked();
		}

		public boolean isCredentialsNonExpired() {
			return delegate.isCredentialsNonExpired();
		}

		public boolean isEnabled() {
			return delegate.isEnabled();
		}

		
	}
	
	public static void main(String[] args) {
		SecureRandom sr = new SecureRandom();
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4, sr);
		System.out.println( bCryptPasswordEncoder.encode("rawPassword") );
		System.out.println( bCryptPasswordEncoder.encode("a") );
		System.out.println( bCryptPasswordEncoder.encode("$2a$10$Yj7aL2pDLet5m5pWlNLlpuNH8YeDXyWDq5Z4d1CzFQaAlAkWz/9xm") );
		System.out.println( bCryptPasswordEncoder.encode("$2a$10$Yj7aL2pDLet5m5pWlNLlpuNH8YeDXyWDq5Z4d1CzFQaAlAkWz/9xm").length() );
	}
	
}
