package test.main;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DESHelper {
	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	public static byte[] encryptDES(byte[] encryptString, String encryptKey)
			throws Exception {

		// IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedData = cipher.doFinal(encryptString);

		return encryptedData;
	}

	public static byte[] decryptDES(byte[] decryptString, String decryptKey)
			throws Exception {

		// IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte decryptedData[] = cipher.doFinal(decryptString);

		return decryptedData;
	}
}