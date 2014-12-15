package hu.jvrs.leo.helper.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Symmetric {
	
	public static SecretAndKey encrypt(String clearText){
		
		SecretKeySpec key = null;		
		byte[] ciphertext = null;
		
		try {			
//			MessageDigest digest = MessageDigest.getInstance("SHA");
//			digest.update(passphrase.getBytes());
//			SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
			
			String passphrase = "correct horse battery staple";
			byte[] salt = "choose a better salt".getBytes();
			int iterations = 10000;
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			SecretKey tmp = factory.generateSecret(new PBEKeySpec(passphrase.toCharArray(), salt, iterations, 128));
			key = new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		
		try {
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");			
			aes.init(Cipher.ENCRYPT_MODE, key);
			ciphertext = aes.doFinal(clearText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		return new SecretAndKey(ciphertext, key);		
	}
	
	public static String decrypt (byte[] ciphertext, SecretKeySpec key) {
//		SecretKeySpec key = null;
		String cleartext = null;
		
//		try {			
//			MessageDigest digest = MessageDigest.getInstance("SHA");
//			digest.update(passphrase.getBytes());
//			SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
			
//			String passphrase = "correct horse battery staple";
//			byte[] salt = "choose a better salt".getBytes();
//			int iterations = 10000;
//			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//			SecretKey tmp = factory.generateSecret(new PBEKeySpec(passphrase.toCharArray(), salt, iterations, 128));
//			key = new SecretKeySpec(tmp.getEncoded(), "AES");
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (InvalidKeySpecException e) {
//			e.printStackTrace();
//		}
		
		
		try {
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
//			aes.init(Cipher.ENCRYPT_MODE, key);
//			byte[] ciphertext = aes.doFinal("my cleartext".getBytes());

			aes.init(Cipher.DECRYPT_MODE, key);
			cleartext = new String(aes.doFinal(ciphertext));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		return cleartext;
	}
	
	public static String makeSym() {			
		SecretKeySpec key = null;
		String cleartext = null;
		byte[] ciphertext = null;
		
		try {			
//			MessageDigest digest = MessageDigest.getInstance("SHA");
//			digest.update(passphrase.getBytes());
//			SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");
			
			String passphrase = "correct horse battery staple";
			byte[] salt = "choose a better salt".getBytes();
			int iterations = 10000;
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			SecretKey tmp = factory.generateSecret(new PBEKeySpec(passphrase.toCharArray(), salt, iterations, 128));
			key = new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		
		try {
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
			aes.init(Cipher.ENCRYPT_MODE, key);
			ciphertext = aes.doFinal("my cleartext".getBytes());

			aes.init(Cipher.DECRYPT_MODE, key);
			cleartext = new String(aes.doFinal(ciphertext));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		System.out.println(ciphertext);
		System.out.println(cleartext);
		return cleartext;
	}
}
