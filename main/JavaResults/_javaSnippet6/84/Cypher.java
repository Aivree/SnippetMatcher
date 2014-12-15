package com.mobileapp.util;


import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Encoder;
import Decoder.BASE64Decoder;


public class Cypher {
	
	private String Algorithm;
	private Cipher ecipher;
	private Cipher dcipher;

	public Cypher (String Algorithm){
		this.Algorithm = Algorithm;
		
	}
	
	private void Encrypter(String key) throws Exception {
		SecretKey secret_key = new SecretKeySpec(key.getBytes(), this.Algorithm);
		ecipher = Cipher.getInstance(this.Algorithm);
	    dcipher = Cipher.getInstance(this.Algorithm);
	    ecipher.init(Cipher.ENCRYPT_MODE, secret_key);
	    dcipher.init(Cipher.DECRYPT_MODE, secret_key);
	}

	private String encryptDES(String str) throws Exception {
	    byte[] utf8 = str.getBytes("UTF8");
		byte[] enc = ecipher.doFinal(utf8);
    	return new BASE64Encoder().encode(enc);
	}

	private String decryptDES(String str) throws Exception {
	    byte[] dec = new BASE64Decoder().decodeBuffer(str);
	    byte[] utf8 = dcipher.doFinal(dec);
	    return new String(utf8, "UTF8");
	}
	  
	private byte[] encryptBlowfish(String key, String plainText) throws GeneralSecurityException {
		  	
		 SecretKey secret_key = new SecretKeySpec(key.getBytes(), this.Algorithm);
		 ecipher = Cipher.getInstance(this.Algorithm);
		 ecipher.init(Cipher.ENCRYPT_MODE, secret_key);
		 return ecipher.doFinal(plainText.getBytes());
	 }
	  
	 private String decryptBlowfish(String key, byte[] encryptedText) throws GeneralSecurityException {

		  SecretKey secret_key = new SecretKeySpec(key.getBytes(), this.Algorithm);
		  dcipher = Cipher.getInstance(this.Algorithm);
		  dcipher.init(Cipher.DECRYPT_MODE, secret_key);
		  byte[] decrypted = dcipher.doFinal(encryptedText);
		  return new String(decrypted);
		  
	 }
	 
	private byte[] encrypt(String key, String value) throws GeneralSecurityException {

		  byte[] raw = key.getBytes(Charset.forName("US-ASCII"));
		  if (raw.length != 16) {
		      throw new IllegalArgumentException("Invalid key size.");
		  }
		  SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		  Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		  cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
		  return cipher.doFinal(value.getBytes(Charset.forName("US-ASCII")));
		  }

	 private static String decrypt(String key, byte[] encrypted) throws GeneralSecurityException {

		    byte[] raw = key.getBytes(Charset.forName("US-ASCII"));
		    if (raw.length != 16) {
		      throw new IllegalArgumentException("Invalid key size.");
		    }
		    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		    cipher.init(Cipher.DECRYPT_MODE, skeySpec,
	        new IvParameterSpec(new byte[16]));
		    byte[] original = cipher.doFinal(encrypted);
		    return new String(original, Charset.forName("US-ASCII"));
		  }
	 
	 
	 
}
