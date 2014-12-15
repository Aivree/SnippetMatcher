package com.x.app.test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	private static final String ALGORITHM_NAME = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

	public static byte[] decrypt(String iv, String key, byte[] input) throws Exception {
		SecretKeySpec spec = new SecretKeySpec(key.getBytes(), ALGORITHM_NAME);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(iv.getBytes()));
		return cipher.doFinal(input);
	}

	public static byte[] encrypt(String iv, String key, byte[] input) throws Exception {
		SecretKeySpec spec = new SecretKeySpec(key.getBytes(), ALGORITHM_NAME);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(iv.getBytes()));
		return cipher.doFinal(input);
	}
}
