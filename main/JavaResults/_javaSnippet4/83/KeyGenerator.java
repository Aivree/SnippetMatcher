/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dv.common.cryptography.aes;

import com.dv.common.file.FileUtilities;
import java.io.File;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created with help from erickson's answer to http://stackoverflow.com/questions/992019/java-256-bit-aes-password-based-encryption
 * @author dan
 */
public class KeyGenerator
{
    public static SecretKey getSecretKey(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException
    {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");   
    }
}
