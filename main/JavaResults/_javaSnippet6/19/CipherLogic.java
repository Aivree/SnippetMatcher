package com.memoler.logic;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

// TODO 可逆暗号にしているが、問題ないのか
/**
 * @author n10o
 */
public class CipherLogic {
    static String cipherAlgorithm = "Blowfish";

    public static byte[] encrypt(String key, String text) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), cipherAlgorithm);
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec);

        return cipher.doFinal(text.getBytes());
    }

    // 実験用。keyを暗号化時と複合化時で違う物を使うとエラー。
    public static String decrypt(String key, byte[] encryptedString) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), cipherAlgorithm);
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, keySpec);

        return new String(cipher.doFinal(encryptedString));
    }

    public static StringBuilder hexalize(byte[] input) {
        final StringBuilder sb = null;
        for (byte b : input)
            sb.append(String.format("%02x", b));
        return sb;
    }
}
