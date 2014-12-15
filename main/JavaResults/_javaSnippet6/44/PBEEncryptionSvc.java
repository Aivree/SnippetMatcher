package org.sen.security.service.encryption;

import javax.crypto.*;


public class PBEEncryptionSvc extends EncryptionSvc {

	public PBEEncryptionSvc(String password) {
		super(password);
	}
	
	public byte[] encryptString(String inputPlainText) throws Exception {
        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.ENCRYPT_MODE, this.getSecretKey(), this.getPBEParameterSpec());
		byte[] byteCipherText = pbeCipher.doFinal(inputPlainText.getBytes());
		return byteCipherText;
	}
	
	public byte[] decryptByteArray(byte[] byteCipherText) throws Exception {				
        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.DECRYPT_MODE, this.getSecretKey(), this.getPBEParameterSpec());
	    byte[] plainText = pbeCipher.doFinal(byteCipherText);
	    return plainText;		
	}

}
