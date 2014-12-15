package me.Totenfluch.CoreClasses;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


public class DataCrypter {
	private static byte[] key = {
		0x6d, 0x6c, 0x5b, 0x12, 0x48, 0x73, 0x41, 0x32, 0x26, 0x63, 0x72, 0x5a, 0x6b, 0x4b, 0x65, 0x79
	};
	private static byte[] key2 = {
			0x2d, 0x2c, 0x5b, 0x12, 0x48, 0x73, 0x41, 0x19, 0x14, 0x6c, 0x17, 0x5b, 0x3a, 0x4b, 0x76, 0x2a
	};
	private static byte[] key3 = {
		0x6a, 0x27, 0x61, 0x11, 0x43, 0x1a, 0x3b, 0x79, 0x76, 0x6a, 0x7a, 0x49, 0x4a, 0x66, 0x18, 0x67
	};

	public static String encrypt(String strToEncrypt){
		try{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			String encryptedString = Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
			return encryptedString;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String encrypt2(String strToEncrypt){
		try{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  SecretKeySpec secretKey = new SecretKeySpec(key2, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			String encryptedString = Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
			return encryptedString;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String encrypt3(String strToEncrypt){
		try{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");  SecretKeySpec secretKey = new SecretKeySpec(key3, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			String encryptedString = Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
			return encryptedString;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String decrypt(String strToDecrypt){
		try{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			String decryptedString = new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
			return decryptedString;

		} catch (Exception e) {
			e.printStackTrace();
			return "Your String is not Encrypted!";
		}
	}
	
	public static String decrypt2(String strToDecrypt)
	{	
		try{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			SecretKeySpec secretKey = new SecretKeySpec(key2, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			String decryptedString = new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
			return decryptedString;

		} catch (Exception e) {
			e.printStackTrace();
			return "Your String is not Encrypted!";
		}
	}
	
	public static String decrypt3(String strToDecrypt)
	{	
		try{
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			SecretKeySpec secretKey = new SecretKeySpec(key3, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			String decryptedString = new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
			return decryptedString;

		} catch (Exception e) {
			e.printStackTrace();
			return "Your String is not Encrypted!";
		}
	}

}
