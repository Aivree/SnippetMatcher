package net.unitemc.common;

import javax.crypto.Cipher;
import java.security.PublicKey;

public class Encryption
{
    public static final Encryption INSTANCE = new Encryption();
    public static final String ALGORITHM = "RSA";

    public PublicKey publicKey;
    //public PrivateKey privateKey;

    public byte[] encrypt(String text)
    {
        byte[] cipherText = null;
        try
        {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherText = cipher.doFinal(text.getBytes());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return cipherText;
    }


//    public String decrypt(byte[] text)
//    {
//        byte[] dectyptedText = null;
//        try
//        {
//            final Cipher cipher = Cipher.getInstance(ALGORITHM);
//            cipher.init(Cipher.DECRYPT_MODE, privateKey);
//            dectyptedText = cipher.doFinal(text);
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }
//        return new String(dectyptedText);
//    }
}
