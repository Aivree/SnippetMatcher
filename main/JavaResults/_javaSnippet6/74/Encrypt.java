package com.xx.java.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class Encrypt {
	
	public static String encrypt(String value) throws Exception
	{
			SecretKeySpec spec = new SecretKeySpec(Entry.keyBytes,"AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, spec);
			byte[] encrypted = cipher.doFinal(value.getBytes());

			return new String(Hex.encodeHex(encrypted));
	}

}
