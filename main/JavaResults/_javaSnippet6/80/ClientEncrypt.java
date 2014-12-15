package encrypt;

import java.security.Key;
import javax.crypto.Cipher;

/**
 *
 * @author Shu Liu
 */
public class ClientEncrypt {

    private final static String ENCRYPT = "DESede";

    private static Key key;

    public static byte[] encrypt(String input) {
        return encrypt(input.getBytes());
    }

    public static byte[] encrypt(char[] input) {
        return encrypt(new String(input));
    }

    public static byte[] encrypt(byte[] input) {
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

    public static String decrypt(byte[] input) {
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

    public static void setKey(Key newKey) {
        key = newKey;
    }
}
