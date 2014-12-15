package org.neocities.goofybud16.crypto;

import javax.crypto.*;

import java.security.*;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.spec.*;

public class CryptoAES
{
	public CryptoAES()
	{
		
	}
	
	public byte[] encrypt(byte[] data, SecretKeySpec key)
	{
		try {
			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");// Get the instance of the Cipher
			aes.init(Cipher.ENCRYPT_MODE, key);// Set the Cipher to encrypt mode and give it a key
			byte[] ciphertext = aes.doFinal(data);// Make the Cipher encrypt
			return ciphertext;// Return the enrypted data
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;// if it fails return null
		}	
	}
	
	public byte[] decrypt(byte[] data, SecretKeySpec key)
	{
		try
		{
		Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");// Get the instance of the Cipher
		aes.init(Cipher.DECRYPT_MODE, key);// Set the cipher to decrypt and give it the key
		return aes.doFinal(data);// Make the Cipher decrypt
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;//if it fails return null
		}
	}

	public SecretKeySpec createKey(String plainTextKey, String plainTextSalt, int saltIterations)
	{
		byte[] salt = plainTextSalt.getBytes();// get the bytes from the salt
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");//Get the instance of the SecretKeyFactory
			SecretKey tmp;// crate a SecretKey
			tmp = factory.generateSecret(new PBEKeySpec(plainTextKey.toCharArray(), salt, saltIterations, 128));// Generate the key, using the "passphrase", the salt, and the iterations
			SecretKeySpec key = new SecretKeySpec(tmp.getEncoded(), "AES");// Get a "SecretKeySpec"
			return key;// return the SecretKeySpec
		} catch (Exception e) {
			e.printStackTrace();
			return null;// If it fails return null
		}
	}
}
