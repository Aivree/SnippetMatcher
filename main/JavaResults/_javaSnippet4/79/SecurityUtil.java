package com.goviami.dartmsg.common.util;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Security Encryption/Decryption Utility class.
 */
public final class SecurityUtil {
	private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
	private static final String SECURE_RANDOM_ALGORITHM_PROVIDER = "SUN";
	private static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final String SECRET_KEY_INSTANCE_AES = "AES";
	private static final String CIPHER_INSTANCE_AES = "AES/CBC/PKCS5Padding";
	private static final String UTF8 = "UTF-8";
	private static final String PASS_KEY = "BULLSEYE_KEY_AUTH";
	private static final int KEY_LENGTH = 256;
	private static final int ITERATIONS = 20000;
	private static byte[] IV = { 0x0a, 0x01, 0x02, 0x03, 0x04, 0x0b, 0x0c, 0x0d, 0x0a, 0x01, 0x02, 0x03, 0x04, 0x0b,
			0x0c, 0x0d };

	/**
	 * Generate Salt Key.
	 * 
	 * @return Salt Byte String
	 * @throws Exception {@link Exception}
	 */
	public static String generateSalt() throws Exception {
		SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM, SECURE_RANDOM_ALGORITHM_PROVIDER);
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		return Base64.getEncoder().encodeToString(salt);
	}

	/**
	 * Encrypt Data with AES algorithm.
	 * 
	 * @param data data to be encrypted
	 * @param salt encryption key
	 * @return encrypted data Byte String
	 * @throws Exception {@link Exception}
	 */
	public static String encryptData(final String data, final String salt) throws Exception {
		byte[] saltBytes = Base64.getDecoder().decode(salt);
		PBEKeySpec spec = new PBEKeySpec(PASS_KEY.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
		SecretKeySpec keySpec = new SecretKeySpec(keyFactory.generateSecret(spec).getEncoded(), SECRET_KEY_INSTANCE_AES);

		Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_AES);
		IvParameterSpec ips = new IvParameterSpec(IV);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ips);
		byte[] encryptedBytes = cipher.doFinal(data.getBytes(UTF8));
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	/**
	 * Decrypt data with AES algorithm.
	 * 
	 * @param encryptedData previously encrypted data
	 * @param salt encryption key
	 * @return decrypted String
	 * @throws Exception {@link Exception}
	 */
	public static String decryptData(final String encryptedData, final String salt) throws Exception {
		byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
		byte[] saltBytes = Base64.getDecoder().decode(salt);
		PBEKeySpec spec = new PBEKeySpec(PASS_KEY.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
		SecretKeySpec keySpec = new SecretKeySpec(keyFactory.generateSecret(spec).getEncoded(), SECRET_KEY_INSTANCE_AES);

		Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_AES);
		IvParameterSpec ips = new IvParameterSpec(IV);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, ips);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		return new String(decryptedBytes);
	}

	/**
	 * Compare the data and authenticate.
	 * 
	 * @param data data to be authenticated
	 * @param encryptedData previously encrypted data
	 * @param salt encryption key
	 * @return boolean is data authenticated
	 * @throws Exception {@link Exception}
	 */
	public static boolean authenticate(final String data, final String encryptedData, final String salt)
			throws Exception {
		final String newEncryptedData = encryptData(data, salt);
		return encryptedData.equals(newEncryptedData);
	}
}
