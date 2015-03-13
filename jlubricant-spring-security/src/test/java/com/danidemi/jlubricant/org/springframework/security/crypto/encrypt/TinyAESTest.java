package com.danidemi.jlubricant.org.springframework.security.crypto.encrypt;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.commons.io.Charsets;
import org.junit.Test;


public class TinyAESTest {
	
	@Test
	public void stringToBytesBackAndForth() {
		
		byte[] bytes = "X".getBytes(Charsets.UTF_8);
		
		String string = new String(bytes, Charsets.UTF_8);
		assertEquals("X", string);

		String string2 = new String(new byte[]{88}, Charsets.UTF_8);
		assertEquals("X", string2);		
		
	}

	@Test
	public void shouldCryptAndEncryptStrings() {
		
		byte[] key = "0123456789012345".getBytes();
		
		TinyAES aes1 = TinyAES.encodingAES(key);
		TinyAES aes2 = TinyAES.decondingAES(key);
		
		String msg =  "a";

		// my_input.getBytes ( Charsets.UTF_8 );
		byte[] original = msg.getBytes( Charsets.UTF_8 );
		byte[] encoded = aes1.apply( original );
		byte[] decoded = aes2.apply( encoded );

		assertEquals( original[0], decoded[0] );		
		assertEquals( original.length, decoded.length );
		assertEquals( msg, new String( decoded, Charsets.UTF_8 ).trim() );
		
	}
	
	@Test
	public void shouldCryptAndEncrypt2() {
		
		byte[] key = "0123456789012345".getBytes();
		
		TinyAES aes1 = TinyAES.encodingAES(key);
		TinyAES aes2 = TinyAES.decondingAES(key);
				
		byte[] original = new byte[]{1};
		byte[] encodedDecoded = aes2.apply( aes1.apply( original ) );
		assert( Arrays.equals(original, encodedDecoded)  );
		
	}
	
}
