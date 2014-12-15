package com.assurant.inc.codelabs.crypto;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/*
 *  Order for encrypt: getBytes, encrypt, encode, toString
 *  Order for decrypt: getBytes, decrypt, decode, toString
 *  Symmetric encryption since it uses same key for encryption and decryption
 */

public class CipherUtil {

	private Key key = null;
	private static final int HASH_BYTE_SIZE = 128; // 512 bits
	
	public CipherUtil() 
	{
		
	}

	private byte[] generateSalt() throws Exception
	{
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte salt[] = new byte[HASH_BYTE_SIZE]; // use salt size at least as long as hash
	    random.nextBytes(salt);
		return salt;
	}
	
	public void createKey() throws Exception {
		String passphrase = "LETSGOROYALS2014";
		int iterations = 10000;
		SecretKeyFactory factory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		SecretKey tmp = factory.generateSecret(new PBEKeySpec(passphrase
				.toCharArray(), generateSalt(), iterations, 128));
		this.key = new SecretKeySpec(tmp.getEncoded(), "AES");

	}

	public Key getSecretKey() {
		return this.key;
	}

	public String encryptAndEncode(String input) throws Exception
	{
		Cipher aes = Cipher.getInstance("AES");
		aes.init(Cipher.ENCRYPT_MODE,key);
		byte[] ciphertext = aes.doFinal(input.getBytes());
        return new Base64(Boolean.TRUE).encodeAsString(ciphertext);		
	}
	
	public String decryptAndDecode(String input) throws Exception
	{
		Cipher aes = Cipher.getInstance("AES");
		aes.init(Cipher.DECRYPT_MODE, key);
		return new String(aes.doFinal(new Base64(Boolean.TRUE).decode(input.getBytes())));
	}
}
