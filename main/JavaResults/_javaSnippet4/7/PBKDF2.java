package com.semicolonapps.crypto;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PBKDF2 {
    private static byte[] ZERO_IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public byte[] decrypt(String encrypted, String password) throws Exception {
        byte[] binary = Base64.decodeBase64(encrypted.getBytes());
        byte[] salt = ZERO_IV;

        if(isSalted(binary)) {
            salt = Array.copy(binary, 8, 16);
            binary = Array.copy(binary, 16, binary.length);
        }

        //SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        //SecretKey derivedKey = secretKeyFactory.generateSecret(keySpec);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 1000, 256);
        SecretKey derivedKey = new PBKDF2KeyImpl(keySpec, "HmacSHA1");

        SecretKeySpec key = new SecretKeySpec(Array.copy(derivedKey.getEncoded(), 0, 16), "AES");
        IvParameterSpec iv = new IvParameterSpec(Array.copy(derivedKey.getEncoded(), 16, 32));

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(binary);
    }

    private static boolean isSalted(byte[] bytes) {
        return ("Salted__".equals(new String(Array.copy(bytes, 0, 8))));
    }
}
