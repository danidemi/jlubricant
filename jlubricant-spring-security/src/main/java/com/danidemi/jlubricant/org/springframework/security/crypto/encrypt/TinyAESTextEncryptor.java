package com.danidemi.jlubricant.org.springframework.security.crypto.encrypt;

import org.apache.commons.io.Charsets;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * A {@link TextEncryptor} based on {@link TinyAES}.
 */
public class TinyAESTextEncryptor implements TextEncryptor {
	
	private final TinyAES enc;
	private final TinyAES dec;
		
	/**
	 * @param key 128/192/256 bit key.
	 */
	public TinyAESTextEncryptor(byte[] key) {
		super();
		this.enc = TinyAES.encodingAES(key);
		this.dec = TinyAES.decondingAES(key);
	}

	/**
	 * Utility constructor that extract the key from an UTF8 string.
	 * @param utf8Key
	 */
	public TinyAESTextEncryptor(String utf8Key) {
		this(utf8Key.getBytes(Charsets.UTF_8));
	}

	@Override
	public String encrypt(String text) {
		byte[] step1 = text.getBytes(Charsets.UTF_8);
		byte[] step2 = enc.apply( step1 );
		byte[] step3 = Base64.encode( step2 );
		return new String(  step3, Charsets.UTF_8 );
	}

	@Override
	public String decrypt(String encryptedText) {
		
		byte[] step3 = encryptedText.getBytes(Charsets.UTF_8);
		byte[] step2 = Base64.decode( step3 );
		byte[] step1 = dec.apply( step2 );
		
		return new String( step1, Charsets.UTF_8 );
	}

}
