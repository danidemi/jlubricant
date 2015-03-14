package com.danidemi.jlubricant.org.springframework.security.crypto.encrypt;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.Charsets;
import org.junit.Test;
import org.springframework.security.crypto.codec.Base64;

public class TinyAESTextEncryptorTest {

	@Test public void shouldNotBeJustABase64Encodings() {
		
		// given
		TinyAESTextEncryptor tested = new TinyAESTextEncryptor("this-is-the-key!");
		
		// when
		String encrypt = tested.encrypt("Hello World");
		
		// then
		assertThat( "Hello World", not(equalTo(new String(  Base64.decode( encrypt.getBytes(Charsets.UTF_8) ), Charsets.UTF_8 ))) );
		
	}
	
	@Test public void shouldCryptAndDecrypt() {
		
		// given
		TinyAESTextEncryptor tested = new TinyAESTextEncryptor("--another-key--!");
		
		// when
		String outcome = tested.decrypt( tested.encrypt("Hello World") );
		
		// then
		assertThat( "Hello World", equalTo(outcome) );
		
	}	
	
}
