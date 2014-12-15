package com.chronicweirdo.protection;

import java.security.Key;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

	private static final String CIPHER_AES_ECB_PKCS5_PADDING = "AES/ECB/PKCS5Padding";
	private static final String SECRET_KEY_SPEC_AES = "AES";
	private static final String SECRET_KEY_FACTORY_PBKDF2_WITH_HMAC_SHA1 = "PBKDF2WithHmacSHA1";

	public static Key key(String passphrase, String salt) throws Exception {
		return key(passphrase, salt.getBytes());
	}
	
	public static Key key(String passphrase, byte[] salt) throws Exception {
		SecretKeyFactory factory = SecretKeyFactory
				.getInstance(SECRET_KEY_FACTORY_PBKDF2_WITH_HMAC_SHA1);
		KeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt, 1024,
				128);
		SecretKey secretKey = factory.generateSecret(keySpec);
		Key key = new SecretKeySpec(secretKey.getEncoded(), SECRET_KEY_SPEC_AES);
		return key;
	}

	public static byte[] encrypt(Key key, byte[] data) throws Exception {
		Cipher aes = Cipher.getInstance(CIPHER_AES_ECB_PKCS5_PADDING);
		aes.init(Cipher.ENCRYPT_MODE, key);
		return aes.doFinal(data);
	}

	public static byte[] decrypt(Key key, byte[] data) throws Exception {
		Cipher aes = Cipher.getInstance(CIPHER_AES_ECB_PKCS5_PADDING);
		aes.init(Cipher.DECRYPT_MODE, key);
		return aes.doFinal(data);
	}
}
