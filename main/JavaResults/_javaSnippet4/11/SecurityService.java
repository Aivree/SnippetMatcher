package com.example.keepmesafe.service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by ironhulk on 2014/07/09.
 */
public class SecurityService {

    private String PBE_ALGORITHM = "BEWithSHA256And256BitAES-CBC-BC";
    private String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    int KEY_SIZE = 256;
    int NUMBER_ITERATIONS = 1000;

    byte[] salt = "thisisaddedsalt".getBytes();
    byte[] iv = "12534567890abcdef".getBytes();

    private SecretKey key;
    private IvParameterSpec ivParameterSpec;

    private SecretKey generateSecretKey(String uniquePass) throws Exception{

        PBEKeySpec pbeKeySpec = new PBEKeySpec(uniquePass.toCharArray(), salt, NUMBER_ITERATIONS,KEY_SIZE);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
        SecretKey tempKey =  secretKeyFactory.generateSecret(pbeKeySpec);
        return new SecretKeySpec(tempKey.getEncoded(),"AES");
    }

    public void buildAlgorithmParameters(String uniquePass) throws Exception{
        key = generateSecretKey(uniquePass);
        ivParameterSpec = new IvParameterSpec(iv);
    }


    public String encryptText(String uniquePass,String text) throws Exception {

        //build params
        buildAlgorithmParameters(uniquePass);

        //encrypt text
        Cipher encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        encryptionCipher.init(Cipher.ENCRYPT_MODE,key,ivParameterSpec);
        byte[] encryptedText = encryptionCipher.doFinal(text.getBytes());

        return new String(encryptedText);
    }

    public String decryptText(String uniquePass,String encryptedText) throws Exception {

        //build params
        buildAlgorithmParameters(uniquePass);

        //encrypt text
        Cipher decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
        decryptionCipher.init(Cipher.DECRYPT_MODE,key,ivParameterSpec);
        byte[] decryptedText = decryptionCipher.doFinal(encryptedText.getBytes());

        return new String(decryptedText);
    }

}
