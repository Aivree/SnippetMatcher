package br.com.jquestion.util.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
	
	SecretKeySpec chave;
    IvParameterSpec iv;
    Cipher cipher;

    public Crypto() throws Exception {
    	    
    	String key = "sh@n_9*63($gy109g!k'n8*6"; 
    	String initializationVector = "1jd*f7_0";
    	
        cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        chave = new SecretKeySpec(key.getBytes("UTF8"), "DESede");
        iv = new IvParameterSpec(initializationVector.getBytes());
    }

    public String encryptText(String original) throws Exception {
        byte[] plaintext = original.getBytes("UTF8");
        cipher.init(Cipher.ENCRYPT_MODE, chave, iv);
        byte[] cipherText = cipher.doFinal(plaintext);
        return new String(Base64Coder.encode(cipherText));
    }

    public String decryptText(String hidden) throws Exception {
        byte[] hiddentext = Base64Coder.decode(hidden.toCharArray());
        cipher.init(Cipher.DECRYPT_MODE, chave, iv);
        byte[] originalText = cipher.doFinal(hiddentext);
        return new String(originalText);
    }

}
