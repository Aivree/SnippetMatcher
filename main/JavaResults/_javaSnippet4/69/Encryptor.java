package Sockets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.spec.KeySpec;

/**
 * Created by Niklas Begley on 18/03/2014.
 *
 */
public class Encryptor
{

    private static SecretKeySpec secret;

    private static IvParameterSpec iv;
    private static Cipher decryptor;
    private static Cipher encryptor;

    public static String decrypt(String message)
    {
        if(secret == null)
        {
            createSecret();
            createIV();
            createEncryptor();
            createDecryptor();
        }
        try
        {
            byte[] encrypted = DatatypeConverter.parseBase64Binary(message);
            byte[] decrypted = decryptor.doFinal(encrypted);
            return new String(decrypted, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String encrypt(String message)
    {
        if(secret == null)
        {
            createSecret();
            createIV();
            createEncryptor();
            createDecryptor();
        }
        try
        {
            byte[] encrypted = encryptor.doFinal(message.getBytes("UTF-8"));
            return DatatypeConverter.printBase64Binary(encrypted);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public static String encryptAndFormat(String message)
    {
        return encrypt(message) + "END";
    }

    private static void createSecret()
    {
        try
        {
            char[] password = new char[]{'f','x','5','6','x','o','r'};
            byte[] salt = new byte[]{1,2,3,4,5,6,7,8};

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
            SecretKey tmp = factory.generateSecret(spec);
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void createEncryptor()
    {
        try
        {
            encryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encryptor.init(Cipher.ENCRYPT_MODE, secret, iv);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void createIV()
    {
        byte[] ivSpec = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        iv = new IvParameterSpec(ivSpec);
    }

    private static void createDecryptor()
    {
        try
        {
            decryptor = Cipher.getInstance("AES/CBC/PKCS5Padding");
            decryptor.init(Cipher.DECRYPT_MODE, secret, iv);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
