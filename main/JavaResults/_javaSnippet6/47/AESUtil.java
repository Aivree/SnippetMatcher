package kr.co.itshan;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AESUtil {

	// 16자리
	private static String basicKey = "all.salgunet.com";
	
	/**
	 * **************************************************************************
	 * @Method 설명 : 암호화하다
	 * @작성일   : 2012. 7. 26. 
	 * @작성자   : salgu2
	****************************************************************************
	 */
	public static String encrypt(String plain) {
		try {
			if( plain == null || plain.isEmpty() )
				return "";
			
			Key key = new SecretKeySpec( basicKey.getBytes(), "AES");
			
			Cipher cipher = Cipher.getInstance("AES");		
			cipher.init( Cipher.ENCRYPT_MODE, key);
			
			return ByteUtils.toHexString( cipher.doFinal( plain.getBytes() ) );			
		}catch(Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	/**
	 * **************************************************************************
	 * @Method 설명 : 해독하다
	 * @작성일   : 2012. 7. 26. 
	 * @작성자   : salgu2
	****************************************************************************
	 */
	public static  String decrypt(String enc) {
		try {
			if( enc == null || enc.isEmpty() )
				return "";
			
			Key key = new SecretKeySpec( basicKey.getBytes(), "AES");
			
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init( Cipher.DECRYPT_MODE, key);
			return new String( cipher.doFinal( ByteUtils.toBytesFromHexString(enc) ) );			
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
