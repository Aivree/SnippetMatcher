package fr.neatmonster.mcprofile.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Security {

    public static String decrypt(final String input, final String key) {
        byte[] output = null;
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            output = cipher.doFinal(Base64.decode(input.getBytes(), Base64.DEFAULT));
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return new String(output);
    }

    public static String encrypt(final String input, final String key) {
        byte[] crypted = null;
        try {
            final SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            crypted = cipher.doFinal(input.getBytes());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return new String(Base64.encode(crypted, Base64.DEFAULT));
    }
}
