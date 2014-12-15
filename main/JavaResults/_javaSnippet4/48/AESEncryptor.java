package com.caibowen.webface.security.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @author bowen.cbw
 * @since 8/27/2014.
 */
public class AESEncryptor implements IEncryptor, Serializable{


    private static final long serialVersionUID = -3565298258295161582L;

    @Inject
    private SecretKeySpec authKey;

    public void setAuthKey(String k) throws Exception {

        byte[] _key = k.getBytes();
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        _key = sha.digest(_key);
        _key = Arrays.copyOf(_key, 16); // use only first 128 bit
        authKey = new SecretKeySpec(_key, "AES");
    }

    //    /* Derive the key, given password and salt. */
//    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//    KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
//    SecretKey tmp = factory.generateSecret(spec);
//    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
//    /* Encrypt the message. */
//    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//    cipher.init(Cipher.ENCRYPT_MODE, secret);
//    AlgorithmParameters params = cipher.getParameters();
//    byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
//    byte[] ciphertext = cipher.doFinal("Hello, World!".getBytes("UTF-8"));
    public byte[] encrypt(byte[] clear){
        try {
            Cipher cipher = Cipher.getInstance("PBKDF2WithHmacSHA1");
            cipher.init(Cipher.ENCRYPT_MODE, authKey);
            return cipher.doFinal(clear);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    /* Decrypt the message, given derived key and initialization vector. */
//    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
//    String plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");
//    System.out.println(plaintext);

    public byte[] decrypt(byte[] obs) {
        try {
            Cipher cipher = Cipher.getInstance("PBKDF2WithHmacSHA1");
            cipher.init(Cipher.DECRYPT_MODE, authKey);
            return cipher.doFinal(obs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
