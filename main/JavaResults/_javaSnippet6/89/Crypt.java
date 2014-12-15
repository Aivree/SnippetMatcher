package com.ardikapras.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public final class Crypt {

    public static String hash(String word) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("SHA-1");
        return new BigInteger(1, m.digest(word.getBytes())).toString(16);
    }

    public static String encrypt(String passphrase, String word)
            throws Exception {

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passphrase.getBytes());

            SecretKeySpec keySpec = new SecretKeySpec(md.digest(), "AES");

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] byteDataToEncrypt = word.getBytes();
            byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
            String strCipherText = Base64.getEncoder().encodeToString(byteCipherText);

            return strCipherText;

        } catch (Exception e) {
            throw e;
        }
    }

    public static String decrypt(String passphrase, String word)
            throws Exception {

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passphrase.getBytes());

            SecretKeySpec keySpec = new SecretKeySpec(md.digest(), "AES");

            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] byteCipherText = Base64.getDecoder().decode(word);

            byte[] byteDecryptedText = aesCipher.doFinal(byteCipherText);
            String strDecryptedText = new String(byteDecryptedText);

            return strDecryptedText;

        } catch (Exception e) {
            throw e;

        }

    }

}
