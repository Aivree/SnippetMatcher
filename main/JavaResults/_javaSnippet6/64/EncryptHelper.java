package com.upreader.helper;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.caucho.util.Base64;

public class EncryptHelper {
	private static SecretKeySpec skeySpec;

	static {
		try {
			byte[] rawPrivateKey = "1upr3@der1!2!3##".getBytes();
			skeySpec = new SecretKeySpec(rawPrivateKey, "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String encrypt(String toEncrypt) {
	    try {
	    	Cipher cipher = Cipher.getInstance("AES");
	    	cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(toEncrypt.getBytes());
			return Base64.encode(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	    
	    return null;
	}

	public static String decrypt(String encrypted) {
	    try {
	    	Cipher cipher = Cipher.getInstance("AES");
	    	cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decodedBytes = Base64.decodeToByteArray(encrypted);
			byte[] original = cipher.doFinal(decodedBytes);
			return new String(original);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    return null;
	}
}
