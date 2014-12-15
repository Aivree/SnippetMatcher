/*package edu.udel.tpic.server.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CipherFactory {
public static String encrypt(String passWord){
		KeyGenerator keyG = KeyGenerator.getInstance("AES");
		keyG.init(256);
		SecretKey secuK = keyG.generateKey();	
		byte[] key = secuK.getEncoded();
		System.out.println("key:"+new String(key));
		SecretKeySpec spec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, spec);
	byte[] encryptData = cipher.doFinal(passWord.getBytes());
	return encryptData.toString();	
	}
	 public String decrypt(String encryptedText){
		 spec = new SecretKeySpec(key, "AES");
			cipher = Cipher.getInstance("AES");	
				cipher.init(Cipher.DECRYPT_MODE, spec);
				byte[] original = cipher.doFinal(encryptData);		
	 }
}*/
