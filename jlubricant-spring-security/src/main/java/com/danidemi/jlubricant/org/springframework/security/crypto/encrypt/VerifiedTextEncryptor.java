package com.danidemi.jlubricant.org.springframework.security.crypto.encrypt;

import static java.lang.String.format;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Encriptors returned by {@link Encryptors} could be easily misconfigured, if
 * for instance the keys are of the wrong length or if Java Cryptography Extension is not properly intalled. 
 * It could happen that the misconfiguration error occurs only at runtime, and if the exception thrown is
 * not at least made clear, you could easily overlook it. This decorator run a
 * series of quick tests upon instantiation to check that the encryptors works
 * correctly.
 * This seems down to the fact that the Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 5.0 
 * needs to be added in order to avoid the issue.
 */
public class VerifiedTextEncryptor implements TextEncryptor {

	private TextEncryptor delegate;

	public VerifiedTextEncryptor(TextEncryptor delegate) {
		super();
		this.delegate = delegate;

		ArrayList<String> samples = new ArrayList<String>(Arrays.asList(
				"astring", "",
				 "0", "01234567890", "_-#é*§"));
		for (String test : samples) {
			try {
				String decrypt = this.decrypt(this.encrypt(test));
				if (!test.equals(decrypt))
					throw new UnsupportedOperationException("not reversable");
			} catch (Exception e) {
				
		        for (Provider provider : Security.getProviders()) {
		            System.out.println("Provider: " + provider.getName());
		            for (Provider.Service service : provider.getServices()) {
		                String algorithm = service.getAlgorithm();
		                String type = service.getType();
						System.out.println("  Algorithm: " + algorithm + " " + type);
		            }
		        }
				
				throw new IllegalStateException(
						format("Underlying encryptor %s failed test '%s' because of: %s",
								delegate, test, e.getMessage()), e);
			}
		}

	}
	

	public String encrypt(String text) {
		return delegate.encrypt(text);
	}

	public String decrypt(String encryptedText) {
		return delegate.decrypt(encryptedText);
	}
	
	public static String dec(String password, String salt, String encString)
	        throws Exception {

	    byte[] ivData = toByte(encString.substring(0, 32));
	    byte[] encData = toByte(encString.substring(32));

	    // get raw key from password and salt
	    PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(),
	            toByte(salt), 50, 256);
	    SecretKeyFactory keyFactory = SecretKeyFactory
	            .getInstance("PBEWithSHA256And256BitAES-CBC-BC");
	    SecretKeySpec secretKey = new SecretKeySpec(keyFactory.generateSecret(
	            pbeKeySpec).getEncoded(), "AES");
	    byte[] key = secretKey.getEncoded();

	    // setup cipher parameters with key and IV
	    KeyParameter keyParam = new KeyParameter(key);
	    CipherParameters params = new ParametersWithIV(keyParam, ivData);

	    // setup AES cipher in CBC mode with PKCS7 padding
	    BlockCipherPadding padding = new PKCS7Padding();
	    BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(
	            new CBCBlockCipher(new AESEngine()), padding);
	    cipher.reset();
	    cipher.init(false, params);

	    // create a temporary buffer to decode into (it'll include padding)
	    byte[] buf = new byte[cipher.getOutputSize(encData.length)];
	    int len = cipher.processBytes(encData, 0, encData.length, buf, 0);
	    len += cipher.doFinal(buf, len);

	    // remove padding
	    byte[] out = new byte[len];
	    System.arraycopy(buf, 0, out, 0, len);

	    // return string representation of decoded bytes
	    return new String(out, "UTF-8");
	}


	private static byte[] toByte(String substring) {
		return substring.getBytes();
	}
	

}
