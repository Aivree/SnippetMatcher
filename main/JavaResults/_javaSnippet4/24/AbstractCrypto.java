package org.abstractcrypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.abstractcrypto.enums.CryptoTypes;
import org.apache.commons.codec.binary.Base64;


public class AbstractCrypto {
	/**
	 * Objecto to perform the encryption
	 */
	protected Cipher cipher;
	/**
	 * Key
	 */
	final protected SecretKey key;
	/**
	 * Salt to encrypt the value with
	 */
	private final static byte[] SALT_VALUE =  new byte[] { 10, 20, 30, 40, 50, 60, 70, 80 };
	/**
	 * Vector to use as encryption
	 */
	protected final static byte[] VECTOR = Base64.decodeBase64("CYHpJrYmqHhPIP4Q1ucDgA==");
	/**
	 * Number of iterations to generate the key
	 */
	private final static int NUMBER_OF_ITERATIONS = 1000;
	/**
	 * Key Lenght
	 */
	private final static int KEY_LENGHT= 256;
	/**
	 * Constructor
	 * @param pass password
	 * @throws Exception 
	 */
	public AbstractCrypto(String keyByte) throws Exception{
		//Key generation
		PBEKeySpec keySpec = new PBEKeySpec(keyByte.toCharArray(), SALT_VALUE, NUMBER_OF_ITERATIONS, KEY_LENGHT);
		SecretKeyFactory factory = SecretKeyFactory.getInstance(CryptoTypes.PBKDF2_WITH_HMAC_SHA1.getCrypto());
		SecretKey tempKey = factory.generateSecret(keySpec);
		key = new SecretKeySpec(tempKey.getEncoded(), CryptoTypes.AES.getCrypto());
	}
	
	protected void initCipher(int encryptMode, byte[] vector) throws Exception{		
		//Cipher inialization
		cipher = Cipher.getInstance(CryptoTypes.AES_CBC_PKCS5.getCrypto());
		cipher.init(encryptMode, key, new IvParameterSpec(vector));
	}
}
