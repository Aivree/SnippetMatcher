package seker.common.encryption;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Des {
    public static String encrypt(String content, String strkey) {
        if (content.equals("")) {
            return "";
        } else
            return doFinal(Cipher.ENCRYPT_MODE, content, strkey);
    }

    public static String decrypt(String content, String strkey) {
        if (content.equals("")) {
            return "";
        } else
            return doFinal(Cipher.DECRYPT_MODE, content, strkey);
    }

    public static String doFinal(int opmode, String content, String strkey) {
        try {
            Key key = new SecretKeySpec(strkey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(opmode, key);

            //
            // make input
            byte plaintext[] = null;
            if (opmode == Cipher.DECRYPT_MODE)
                plaintext = Base64.decode(content, Base64.DEFAULT);
            else
                plaintext = content.getBytes("UTF-8");

            byte[] output = cipher.doFinal(plaintext);

            //
            // make output
            String Ciphertext = null;
            if (opmode == Cipher.DECRYPT_MODE)
                Ciphertext = new String(output);
            else
                Ciphertext = Base64.encodeToString(output, Base64.DEFAULT);

            return Ciphertext;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[] encrypt(byte[] content, String strkey) {
        if (null == content || content.length == 0) {
            return null;
        } else
            return doFinal(Cipher.ENCRYPT_MODE, content, strkey);
    }

    public static byte[] decrypt(byte[] content, String strkey) {
        if (null == content || content.length == 0) {
            return null;
        } else
            return doFinal(Cipher.DECRYPT_MODE, content, strkey);
    }

    public static byte[] doFinal(int opmode, byte[] content, String strkey) {
        try {
            Key key = new SecretKeySpec(strkey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(opmode, key);

            //
            // make input
            byte plaintext[] = null;
            if (opmode == Cipher.DECRYPT_MODE)
                plaintext = Base64.decode(content, Base64.DEFAULT);
            else
                plaintext = content;

            byte[] output = cipher.doFinal(plaintext);

            //
            // make output
            byte[] Ciphertext = null;
            if (opmode == Cipher.DECRYPT_MODE)
                Ciphertext = output;
            else
                Ciphertext = Base64.encode(output, Base64.DEFAULT);

            return Ciphertext;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
