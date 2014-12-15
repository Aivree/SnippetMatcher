package test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption{
	
	private static final String KEY = "ThisIsASecretKey";
	private static final String ALGORITHM = "AES";
	private static final String ENCODE = "UTF-8";
	
	public static byte[] encrypt(String text){
		try{
			Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
			return cipher.doFinal(text.getBytes(ENCODE));
		} catch (Exception e) {e.printStackTrace();}
		return new byte[0];
	}
	
	public static String decrypt(byte[] encrypted){
		try{
			Cipher cipher = initCipher(Cipher.DECRYPT_MODE);
			return new String(cipher.doFinal(encrypted));
		} catch (Exception e) {e.printStackTrace();}
		return "";
	}
	
	private static Cipher initCipher(int CIPHER_MODE){
		try{
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(ENCODE), ALGORITHM);
			cipher.init(CIPHER_MODE, secretKeySpec);
			return cipher;
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
}