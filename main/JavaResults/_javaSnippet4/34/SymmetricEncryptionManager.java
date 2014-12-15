package com.jmpm.utils.encryption;

import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricEncryptionManager extends EncryptionManager {
	
	final static private int numBits = 128;
	final static private int iterations = 10000;
	final static private int saltSizeBytes = 50;

	
	public static byte[] generateKey() throws Exception {
		SecretKeySpec key = new SecretKeySpec(getRandomBytes(numBits / 8), "AES");
		return key.getEncoded();

	}
	
	public static byte[] generateRandomSaltedKeyFromPassword(String password) throws Exception {
		byte[] keySalt = getRandomBytes(saltSizeBytes);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecretKey tmp = factory.generateSecret(new PBEKeySpec(password.toCharArray(), keySalt, iterations, numBits));
		SecretKeySpec key = new SecretKeySpec(tmp.getEncoded(), "AES");
		return key.getEncoded();
	}
	
	private static byte[] getRandomBytes(int numBytes) {
		final Random r = new SecureRandom();
		byte[] randomBytes = new byte[numBytes];
		r.nextBytes(randomBytes);
		return randomBytes;
	}	
	
	public static byte[] encrypt(byte[] message, byte[] key) throws Exception {
		Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
		return aes.doFinal(message);
	}
	
	public static byte[] decrypt(byte[] message, byte[] key) throws Exception {
		Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aes.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
		return aes.doFinal(message);
	}	


}
