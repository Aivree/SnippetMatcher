/**
 * 
 */
package com.zazzercode.robot.controllers.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * @author prayag
 * 
 */
public class AuthenticationManager {
	private static String algorithm = "DESede";
	private static Key key = null;
	private static Cipher cipher = null;

	public static byte[] encrypt(String input) throws Exception {
		key = KeyGenerator.getInstance(algorithm).generateKey();
		cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] inputBytes = input.getBytes();
		return cipher.doFinal(inputBytes);
	}

	public static String decrypt(byte[] encryptionBytes) throws Exception {
		key = KeyGenerator.getInstance(algorithm).generateKey();
		cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] recoveredBytes = cipher.doFinal(encryptionBytes);
		String recovered = new String(recoveredBytes);
		return recovered;
	}
}
