package com.lifesights.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {
	private static char[] password = "NCO664aet34668af4a6355f6b".toCharArray();
	private static byte[] salt = "oijsdfvb98y46".getBytes();
	private static String textFormat = "UTF-8";
	private static String algorithm = "AES/CBC/PKCS5Padding";
	static SecretKeyFactory factory;
	static KeySpec spec;
	static SecretKey tmp;
	static SecretKeySpec secretKey;
	private static Cipher serverEncryptCipher;
	private static Cipher serverDecryptCipher;
	private static IvParameterSpec ivParameterSpec;
	static {
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			spec = new PBEKeySpec(password, salt, 65536, 128);
			tmp = factory.generateSecret(spec);
			secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
			serverEncryptCipher = Cipher.getInstance(algorithm);
			serverEncryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
			ivParameterSpec = new IvParameterSpec(serverEncryptCipher.getIV());

			serverDecryptCipher = Cipher.getInstance(algorithm);
			serverDecryptCipher.init(Cipher.DECRYPT_MODE, secretKey,
					ivParameterSpec);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeyException | InvalidKeySpecException
				| InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	public static byte[] encrypt(String text) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException {
		return serverEncryptCipher.doFinal(text.getBytes(textFormat));
	}

	public static String decrypt(byte[] ctext) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException {
		return new String(serverDecryptCipher.doFinal(ctext), textFormat);
	}

	@Deprecated
	public static byte[] symmetricEncrypt(String text, char[] key)
			throws UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeySpecException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException {
		KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), algorithm);
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		return cipher.doFinal(text.getBytes(textFormat));

	}

	@Deprecated
	public static String symmetricDecrypt(byte[] ctext, String key) {

		return key;
	}
}
