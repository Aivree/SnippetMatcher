package com.yellowaxe.testing;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class BlowfishTester {

	private static String key = "key12key12key120a";
	private static String plainText = "This is the plain text;";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(ByteArrayUtils.ByteArrayToHexString(plainText.getBytes()));

        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        Cipher decipher = Cipher.getInstance("Blowfish");
        decipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = decipher.doFinal(encrypted);

        System.out.println(ByteArrayUtils.ByteArrayToHexString(decrypted));
	}

}
