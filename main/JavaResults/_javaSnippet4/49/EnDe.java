package com.ngy.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class EnDe {
	
	// Algorithm used

    private final static String ALGORITHM = "AES";

     

    /**

     * Encrypt data

     * @param secretKey -   a secret key used for encryption

     * @param data      -   data to encrypt

     * @return  Encrypted data

     * @throws Exception

     */

    public static String cipher(String secretKey, String data) throws Exception {
        return toHex(cipher(secretKey, data.getBytes()));
    }
    
    public static byte[] cipher(String secretKey, byte[] data) throws Exception {

        

    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    	KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), secretKey.getBytes(), 128, 256);

    	SecretKey tmp = factory.generateSecret(spec);

    	SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

    	

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, key);

         

        return cipher.doFinal(data);

    }

     

    /**

     * Decrypt data

     * @param secretKey -   a secret key used for decryption

     * @param data      -   data to decrypt

     * @return  Decrypted data

     * @throws Exception

     */

    public static String decipher(String secretKey, String data) throws Exception {        
        return new String(decipher(secretKey,toByte(data)));

    }
    
    public static byte[] decipher(String secretKey, byte[] data) throws Exception {

        

    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

    	KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), secretKey.getBytes(), 128, 256);

    	SecretKey tmp = factory.generateSecret(spec);

    	SecretKey key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

    	

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        

        cipher.init(Cipher.DECRYPT_MODE, key);

         

        return cipher.doFinal(data);
    }

     

    // Helper methods

     

    private static byte[] toByte(String hexString) {

        int len = hexString.length()/2;

         

        byte[] result = new byte[len];

         

        for (int i = 0; i < len; i++)

            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();

        return result;

    }

 

    public static String toHex(byte[] stringBytes) {

        StringBuffer result = new StringBuffer(2*stringBytes.length);

         

        for (int i = 0; i < stringBytes.length; i++) {

            result.append(HEX.charAt((stringBytes[i]>>4)&0x0f)).append(HEX.charAt(stringBytes[i]&0x0f));

        }

         

        return result.toString();

    }

     

    private final static String HEX = "0123456789ABCDEF";

}
