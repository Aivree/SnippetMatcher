package Hisecure;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import Decoder.BASE64Decoder;


public class function_AES {

	private static String key = "1AF4567SD123TR67";
	public static String encrypt(String input){
		byte[] crypted = null;
		try{
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(input.getBytes());
		}catch(Exception e){
			System.out.println(e.toString());
		}
		return (new sun.misc.BASE64Encoder()).encode(crypted) ;
	}

	public static String decrypt(String input) {
		byte[] output = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(new BASE64Decoder().decodeBuffer(input));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new String(output);
	}
	
	
}
