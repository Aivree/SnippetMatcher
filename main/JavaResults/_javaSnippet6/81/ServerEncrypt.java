package encrypt;

import java.security.Key;
import javax.crypto.Cipher;

/**
 *
 * @author Shu Liu
 */
public class ServerEncrypt {

    private final static String ENCRYPT = "DESede";

    public static byte[] encrypt(String input, Key key) {
        return encrypt(input.getBytes(), key);
    }

    public static byte[] encrypt(char[] input, Key key) {
        return encrypt(new String(input), key);
    }

    public static byte[] encrypt(byte[] input, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherText = cipher.doFinal(input);

            return cipherText;
        } catch (Exception ex) {
            System.out.println("Error when encrypt " + ex.toString());
            return input;
        }
    }

    public static String decrypt(byte[] input, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPT);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] plainText = cipher.doFinal(input);

            return new String(plainText);
        } catch (Exception ex) {
            System.out.println("Error when decrypt " + ex.toString());
            return new String(input);
        }
    }
}
