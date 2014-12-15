package com.palm.encrypt.jce.rsa;

import java.security.KeyPair;

import javax.crypto.Cipher;

/**
 * rsa加密解密
 * 
 * @author weixiang.qin
 * 
 */
public class RsaEncDemo {
	
	/**
	 * 公钥加密
	 * 
	 * @param key
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public byte[] publicEncrypt(KeyPair key, String str) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key.getPublic());
		return cipher.doFinal(str.getBytes("UTF8"));
	}

	/**
	 * 公钥解密
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public byte[] publicDecrypt(KeyPair key, byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, key.getPublic());
		return cipher.doFinal(data);
	}

	/**
	 * 私钥加密
	 * 
	 * @param key
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public byte[] privateEncrypt(KeyPair key, String str) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key.getPrivate());
		return cipher.doFinal(str.getBytes("UTF8"));
	}

	/**
	 * 私钥解密
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public byte[] privateDecrypt(KeyPair key, byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
		return cipher.doFinal(data);
	}
}
