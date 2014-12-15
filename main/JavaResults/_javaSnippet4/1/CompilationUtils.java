package compiler.mocha;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/** WIP compiler */
public class CompilationUtils {

	public static String compileString(String string) {

		try {

			byte[] salt = "mocha-compiler".getBytes();
			
			int iterations = 10000;
			
			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
			
			SecretKey tmp = factory.generateSecret(new PBEKeySpec(string
					.toCharArray(), salt, iterations, 128));
			
			SecretKeySpec key = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
			aes.init(Cipher.ENCRYPT_MODE, key);
			
			byte[] ciphertext = aes.doFinal("mocha-compiler".getBytes());

			return new String(aes.doFinal(ciphertext));

		} catch (Exception e) {

			e.printStackTrace();

			return null;
		}
	}

	public static String decompileString(String string) {

		try {

			byte[] salt = "mocha-compiler".getBytes();
			
			int iterations = 10000;
			
			SecretKeyFactory factory = SecretKeyFactory
					.getInstance("PBKDF2WithHmacSHA1");
			
			SecretKey tmp = factory.generateSecret(new PBEKeySpec(string
					.toCharArray(), salt, iterations, 128));
			
			SecretKeySpec key = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
			aes.init(Cipher.DECRYPT_MODE, key);
			
			byte[] ciphertext = aes.doFinal();

			return new String(aes.doFinal(ciphertext));

		} catch (Exception e) {

			e.printStackTrace();

			return null;
		}
	}
}
