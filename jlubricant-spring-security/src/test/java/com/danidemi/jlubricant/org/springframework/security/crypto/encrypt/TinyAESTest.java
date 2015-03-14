package com.danidemi.jlubricant.org.springframework.security.crypto.encrypt;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.Charsets;
import org.junit.Test;


public class TinyAESTest {
	
	@Test
	public void justShowHowConversionBetweenBytesAndStringWorks() {
		
		// given
		// ...a string represented as bytes
		byte[] bytes = "X".getBytes(Charsets.UTF_8);
		
		// ...and the bytes back to string
		String fromBytes = new String(bytes, Charsets.UTF_8);
		
		// ...and the string created busing directly the byte
		String fromNumber = new String(new byte[]{88}, Charsets.UTF_8);
		
		// then
		// ...they all should be equals
		assertEquals("X", fromBytes);
		assertEquals("X", fromNumber);		
		
	}

	@Test
	public void shouldCryptAndEncryptStrings() {
		
		// given
		// ...a key
		byte[] key = "0123456789012345".getBytes();
		
		// ...an encoder to crypt
		TinyAES encoder = TinyAES.encodingAES(key);
		
		// ...a decoder to decrypt
		TinyAES decoder = TinyAES.decondingAES(key);
		
		// ...a message 
		String[] msgs = new String[]{"Hello World!", "", "---"};
		
		// then
		for (String msg : msgs) {
			assertArrayEquals( msg.getBytes( Charsets.UTF_8 ), encodeAndDecodeAsBytes(msg.getBytes( Charsets.UTF_8 ), encoder, decoder) );		
			assertEquals( msg, encodeAndDecodeAsString(msg, encoder, decoder) );			
		}
		
	}
	
	@Test public void shouldNotDecryptCorrectlyWhenKeyIsWrong() {
		
		// given
		// ...two incopatible AESs
		TinyAES encoder = TinyAES.encodingAES("key1key1key1key1".getBytes());
		TinyAES decoder = TinyAES.decondingAES("key2key2key2key2".getBytes());
		
		// then
		assertThat( "Hello", not(equalTo( encodeAndDecodeAsString("Hello", encoder, decoder) )) );
		
		
	}
			
	String encodeAndDecodeAsString(String msg, TinyAES encoder, TinyAES decoder){
		byte[] decoded = encodeAndDecodeAsBytes(msg, encoder, decoder);
		return new String( decoded, Charsets.UTF_8 );
	}

	byte[] encodeAndDecodeAsBytes(String msg, TinyAES encoder, TinyAES decoder) {
		byte[] msgBytes = msg.getBytes( Charsets.UTF_8 );
		return encodeAndDecodeAsBytes(msgBytes, encoder, decoder);
	}

	byte[] encodeAndDecodeAsBytes(byte[] msgBytes, TinyAES encoder, TinyAES decoder) {
		byte[] encoded = encoder.apply( msgBytes );
		byte[] decoded = decoder.apply( encoded );
		return decoded;
	}
}
