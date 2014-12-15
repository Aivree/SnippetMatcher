/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author tux
 */
public class Cript {

    private byte[] salt;
    private PBEKeySpec ks;
    private SecretKey skey;
    private SecretKeyFactory skf;
    private BASE64Decoder dec;
    private BASE64Encoder enc;

    public Cript() {
        dec = new BASE64Decoder();
        enc = new BASE64Encoder();
    }

    public void senha(char[] chars) {
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            salt = new byte[4]; // deixamos 4 bytes zerados :(  
            ks = new PBEKeySpec(chars, salt, 100, 128);
            skey = new SecretKeySpec(skf.generateSecret(ks).getEncoded(), "AES");
        } catch (Exception ex) {
            BLog.error("Cript: " + ex.getMessage());

        }
    }

    public String cifrar(String original) {
        Cipher cf;
        try {
            cf = Cipher.getInstance("AES/OFB/NoPadding");
            cf.init(Cipher.ENCRYPT_MODE, skey, new IvParameterSpec(new byte[16]));
            return enc.encode(cf.doFinal(original.getBytes()));
        } catch (Exception ex) {
            BLog.error("Cript: " + ex.getMessage());

        }
        return null;
    }

    public String decifrar(String cifrado) {
        try {
            Cipher cf = Cipher.getInstance("AES/OFB/NoPadding");
            cf.init(Cipher.DECRYPT_MODE, skey, new IvParameterSpec(new byte[16]));
            return new String(cf.doFinal(dec.decodeBuffer(cifrado)));
        } catch (Exception ex) {
            BLog.error("Cript: " + ex.getMessage());
        }
        return null;
    }
}
