package com.danidemi.jlubricant.org.springframework.security.crypto.encrypt;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TinyAESTextEncryptorSuccessTest {

	@Parameters
	public static List<Object[]> params() {
			return Arrays.asList( new Object[][]{
					new String[]{"0123456701234567", "hello"},
					new String[]{"0123456701234567", ""},
					new String[]{"0123456701234567", "VERYveryVERYveryVERYveryVERYveryVERYveryVERYveryLong"},
			});
	}

	private String toEncryptAndDecrypt;
	private String key;
	
	public TinyAESTextEncryptorSuccessTest(String key, String toEncryptAndDecrypt) {
		this.key = key;
		this.toEncryptAndDecrypt = toEncryptAndDecrypt;
	}
	
	@Test
	public void shouldEncryptAndDecrypt(){
		TinyAESTextEncryptor encryptor = new TinyAESTextEncryptor(key);
		assertThat( toEncryptAndDecrypt, equalTo( encryptor.decrypt( encryptor.encrypt(toEncryptAndDecrypt) ) ) );
	}
	
}
