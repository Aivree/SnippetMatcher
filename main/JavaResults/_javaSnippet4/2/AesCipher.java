package com.samples.oroshisoba.cipher;

import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AesCipher {

	private static final String PBKDF2 = "PBKDF2WithHmacSha1";
	private static final String AES = "AES";
	private static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";

	public byte[] encrypto(String base) throws Exception {

		// Create SecretKey by hmac
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2);
		KeySpec pbeKeySpec = new PBEKeySpec("password".toCharArray(), "salt".getBytes(), 1023, 128);
		SecretKey pbeSecretKey = keyFactory.generateSecret(pbeKeySpec);
		// Create SecretKey for AES by KeyFactory-With-Hmac
		SecretKey aesSecretKey = new SecretKeySpec(pbeSecretKey.getEncoded(), AES);
		
		Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
//		cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey, SecureRandom.getInstance("SHA1PRNG"));
		cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey);
		byte[] encrypted = cipher.doFinal(base.getBytes());

		System.out.println("IV: " + new String(cipher.getIV()));
		System.out.println("AlgorithmParameter: " + cipher.getParameters().getEncoded());
		Cipher decryptor = Cipher.getInstance(AES_CBC_PKCS5PADDING);
		decryptor.init(Cipher.DECRYPT_MODE, aesSecretKey, new IvParameterSpec(cipher.getIV()));
		System.out.println("Decrypted: " + new String(decryptor.doFinal(encrypted)));
		
		return encrypted;
	}
}



