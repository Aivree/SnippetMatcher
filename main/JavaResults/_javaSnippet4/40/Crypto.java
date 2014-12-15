package com.llys.bootes.util;


import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Crypto {
    
    private String password;
    private String saltValue;
    private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5, 6 };
    
    public Crypto() {
        
    }
    public void init(String password, String saltValue) {
        this.password = password;
        this.saltValue = saltValue;
    }
    

    public byte[] encrypt(String message) throws Exception {
        
        byte[] salt = saltValue.getBytes();
        
        
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
        byte[] ciphertext = cipher.doFinal(message.getBytes("UTF-8"));
        return ciphertext;
    } 
    

    public String decrypt(byte[] enc) throws Exception {
        
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), saltValue.getBytes(), 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        String plaintext = new String(cipher.doFinal(enc), "UTF-8");
        return plaintext;
    }
    
 
    public void testCrypt() throws Exception{
        String password = "akvotmdnjem";
        String salt = "akdlthfxm";
        Crypto cr = new Crypto();
        cr.init(password, salt);
        byte[] enc = cr.encrypt("skskasTmwl");
        System.out.println("enc : " + enc);
        
        byte[] byteT = Base64.encodeBase64(enc);
        String byteTest = Base64.encodeBase64String(enc);
        
        for(int i = 0; i < byteT.length; i++) {
            System.out.print(byteT[i]);
        }
        System.out.print(byteT.toString() + "\n");
        System.out.print(byteTest + "\n");
        byte[] org2 = Base64.decodeBase64(byteTest);
        String org = cr.decrypt(org2);
        System.out.println("org : " + org);
    }    
}
