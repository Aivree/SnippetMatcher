package freeencryptrepo.cipher;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AES{

	public String Encrypt(String _key, String salt, String message) throws GeneralSecurityException 
	{
	  Cipher cipher = Cipher.getInstance("AES");
	  MessageDigest md = MessageDigest.getInstance("MD5");
	  SecretKeySpec key = new SecretKeySpec(md.digest((salt+_key).getBytes()), "AES");
	  cipher.init(Cipher.ENCRYPT_MODE, key);
	  
	  return new String(cipher.doFinal((salt+message).getBytes()));
	}
	
	public String Decrypt(String _key, String salt, String message) throws GeneralSecurityException 
	{
	  Cipher cipher = Cipher.getInstance("AES");
	  MessageDigest md = MessageDigest.getInstance("MD5");
	  SecretKeySpec key = new SecretKeySpec(md.digest((salt+_key).getBytes()), "AES");
	  cipher.init(Cipher.DECRYPT_MODE, key);
	  
	  return new String(cipher.doFinal((message.substring(salt.length())).getBytes()));
	}

}