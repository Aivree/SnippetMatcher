package com.frtive.core.crypto;

import com.frtive.core.FrtiveApplication;
import sun.org.mozilla.javascript.internal.EcmaError;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;

/**
 * Created by matt on 4/21/14.
 */
public class FrtiveSecretKey {
	// Store the final key
	private SecretKey key;
	private String header = "*FRTIVE*";

	public FrtiveSecretKey(String email, String password, FrtiveApplication app) throws Exception {
		// Get the salt
		String salt = header+email+app.getId();

		// Generate the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 50000, 128);
		SecretKey tmp = factory.generateSecret(spec);
		key = new SecretKeySpec(tmp.getEncoded(), "AES");
	}

	public FrtiveSecretKey(String email, String key, FrtiveApplication app, boolean useKey) throws Exception {
		// Retrieve the key
		this.key = new SecretKeySpec(EncryptedString.stringToBytes(key), "AES");
	}

	public EncryptedString encrypt(String text) throws Exception {
		// Insert the header for verifying the result
		text = header+text;

		// Do the encryption using this key
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		AlgorithmParameters params = cipher.getParameters();
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

		// Return the result as an encrypted string.
		return new EncryptedString(cipher.doFinal(text.getBytes("UTF-8")), iv);
	}

	public String decrypt(EncryptedString text) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(text.getIV()));
		String plaintext = new String(cipher.doFinal(text.getBytes()), "UTF-8");

		if (plaintext.startsWith(header)) {
			return plaintext.substring(header.length());
		} else {
			return null;
		}
	}

	public String toString() {
		return EncryptedString.bytesToString(key.getEncoded());
	}
}
