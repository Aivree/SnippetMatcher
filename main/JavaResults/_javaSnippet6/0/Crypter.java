package network;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;


public class Crypter {




		public static String encrypte(String password){
			try
			{
				Key clef = new SecretKeySpec("monksdescailloux".getBytes("ISO-8859-2"),"Blowfish");
				Cipher cipher=Cipher.getInstance("Blowfish");
				cipher.init(Cipher.ENCRYPT_MODE,clef);
				return new String(cipher.doFinal(password.getBytes()));
			}
			catch (Exception e)
			{
				return null;
			}
		}

	
		public static String decrypte(String password){
			try
			{
				Key clef = new SecretKeySpec("monksdescailloux".getBytes("ISO-8859-2"),"Blowfish");
				Cipher cipher=Cipher.getInstance("Blowfish");
				cipher.init(Cipher.DECRYPT_MODE,clef);
				return new String(cipher.doFinal(password.getBytes()));
			}
			catch (Exception e)
			{
				System.out.println(e);
				return null;
			}
		}
		
}
