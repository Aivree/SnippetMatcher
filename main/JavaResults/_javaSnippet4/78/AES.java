/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptcod.helpers;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author Gian
 */
public class AES {
    private static final String ALGORITHM = "AES";
    private static final String MODE = "GCM";
    private static final String PADDING = "NoPadding";
    //private static final String PADDING = "PKCS5Padding";
    private static final String TESTTEXT = "1dfdsfsdfsdfsdf sdfsdfsdfsdfds fsd fsdfsdfsdfsdfsdfsdfsdf";
    private static final String ENCODING = "UTF-8"; 
   
    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        
        String password = "TESTPASS";
        String salt = "1234";
        
        /* Derive the key, given password and salt. */
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
        
        /* Encrypt the message. */
        
        //GCMParameterSpec test = new GCMParameterSpec();
        
        
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + MODE + "/" + PADDING, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
        System.out.println("IV length: " + iv.length * 8);
        byte[] ciphertext = cipher.doFinal(TESTTEXT.getBytes(ENCODING));
        System.out.println(new String(ciphertext, ENCODING));
        System.out.println(ciphertext.length);
        ciphertext[45] = 6;
        
        /* Decrypt the message, given derived key and initialization vector. */
        Cipher cipher2 = Cipher.getInstance(ALGORITHM + "/" + MODE + "/" + PADDING, "BC");
        cipher2.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
        String plaintext = new String(cipher2.doFinal(ciphertext), ENCODING);
        System.out.println(plaintext);
    }
}
