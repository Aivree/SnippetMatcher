package core.security;

import javax.crypto.*;
import javax.crypto.spec.*;

import org.apache.commons.codec.binary.Base64;
//import org.apache.commons.codec.;

import java.security.SecureRandom;

public class Encryption {
	
	static String str_raw = "ThisIsASecretKey";
	
	static String pss_str_raw = "ThisIsASecretKey";
	
	static SecureRandom rnd = new SecureRandom();
	
	static IvParameterSpec iv = new IvParameterSpec(str_raw.getBytes());

	public static String encrypt(String value) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(str_raw.getBytes(),
					"AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(value.getBytes());

			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String encrypted) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(str_raw.getBytes(),
					"AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static String pss_encrypt(String value) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(pss_str_raw.getBytes(),
					"AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(value.getBytes());

			return Base64.encodeBase64String(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String pss_decrypt(String encrypted) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(pss_str_raw.getBytes(),
					"AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		String encrypt = encrypt("root");
		System.out.println("encrypted string:" + encrypt);
		System.out.println("decrypted value:" + (decrypt(encrypt)));
	}
}