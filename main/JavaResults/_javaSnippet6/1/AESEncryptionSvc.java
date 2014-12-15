package org.sen.security.service.encryption;

import javax.crypto.Cipher;

public class AESEncryptionSvc extends EncryptionSvc {

	public AESEncryptionSvc() {
		super();
	}
	
	public byte[] encryptString(String inputPlainText) throws Exception {
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
		byte[] byteCipherText = aesCipher.doFinal(inputPlainText.getBytes());
		
		return byteCipherText;
	}
	
	public byte[] decryptByteArray(byte[] byteCipherText) throws Exception {				
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE, getSecretKey());		
	    byte[] plainText = aesCipher.doFinal(byteCipherText);
	    
	    return plainText;		
	}

}
