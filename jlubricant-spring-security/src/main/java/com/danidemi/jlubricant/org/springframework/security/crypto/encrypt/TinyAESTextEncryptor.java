package com.danidemi.jlubricant.org.springframework.security.crypto.encrypt;

import org.apache.commons.io.Charsets;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public class TinyAESTextEncryptor implements TextEncryptor {
	
	private TinyAES enc;
	private TinyAES dec;
		
	public TinyAESTextEncryptor(byte[] key) {
		super();
		this.enc = TinyAES.encodingAES(key);
		this.enc = TinyAES.decondingAES(key);
	}

	public TinyAESTextEncryptor(String string) {
		this(string.getBytes(Charsets.UTF_8));
	}

	@Override
	public String encrypt(String text) {
		return new String(  Base64.encode( text.getBytes(Charsets.UTF_8) ), Charsets.UTF_8 );
	}

	@Override
	public String decrypt(String encryptedText) {
		return new String(  Base64.decode( encryptedText.getBytes(Charsets.UTF_8) ), Charsets.UTF_8 );
	}

}
