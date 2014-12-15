package halo.akwei.enc;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DESUtil {

	private Cipher encryptCipher = null;

	private Cipher decryptCipher = null;

	public static final String DES_ECB_PKCS5Padding = "DES/ECB/PKCS5Padding";

	public DESUtil(byte[] keyBytes, String transformation) {
		try {
			Key key = new SecretKeySpec(keyBytes, "DES");
			encryptCipher = Cipher.getInstance(transformation);
			encryptCipher.init(Cipher.ENCRYPT_MODE, key);
			decryptCipher = Cipher.getInstance(transformation);
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] getKey(String key) {
		return key.substring(0, 8).getBytes();
	}

	public byte[] encrypt(byte[] data) {
		try {
			return encryptCipher.doFinal(data);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public byte[] decrypt(byte[] data) {
		try {
			return decryptCipher.doFinal(data);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
