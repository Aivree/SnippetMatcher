package cryto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class CryptoManagerImp implements ICryptoManager {

	@Override
	public String encrypt(String data, SecureKey key) {
		
		SecretKeySpec aeskey = new SecretKeySpec(key.getBytes(), "AES");
		try {
			Cipher cipher = Cipher.getInstance("AES");
	        cipher.init(Cipher.ENCRYPT_MODE, aeskey);
	        byte[] cipherbyte = cipher.doFinal(data.getBytes("UTF-8"));
	        String encrypt = new Base64().encodeAsString(cipherbyte);
	        return encrypt;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String decrypt(String encrypted, SecureKey key) {
		try {
			SecretKeySpec aeskey = new SecretKeySpec(key.getBytes(), "AES");
	        Cipher c = Cipher.getInstance("AES");
	        c.init(Cipher.DECRYPT_MODE, aeskey);
	        byte[] encryptedbyte = c.doFinal(new Base64().decode(encrypted.getBytes()));
	        String decryptedValue =  new String(encryptedbyte );
	        return decryptedValue;
				} catch (Exception e) {
					e.printStackTrace();
				}
		    return null;
	}

}
