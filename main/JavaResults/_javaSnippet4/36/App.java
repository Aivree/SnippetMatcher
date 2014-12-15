package org.sonatype.mavenbook;

import javax.crypto.CipherOutputStream;
import javax.crypto.Cipher;
import java.security.Key;
import java.security.spec.KeySpec;
import java.security.Security;
import javax.crypto.SecretKeyFactory;
import java.security.AlgorithmParameters;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.KeyGenerator;
import java.security.SecureRandom;
import java.io.ByteArrayOutputStream;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class App 
{
    public static void main( String[] args )
    {

        Security.addProvider(new BouncyCastleProvider());

        try
        {
            String jceProviderName = BouncyCastleProvider.PROVIDER_NAME;
            String plainText = "plainText";
            String password = "password";
            byte[] salt = new byte[8];

            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PKCS5S2", jceProviderName);
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 1024, 256);
            SecretKey tmp = keyFactory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/EAX/NoPadding", jceProviderName);
            cipher.init(Cipher.ENCRYPT_MODE, secret);

            AlgorithmParameters params = cipher.getParameters();
            //byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
            String ciphertext = cipher.doFinal(plainText.getBytes()).toString();

            System.out.println(ciphertext);
        }
        catch (Exception e)
        {
            System.err.println(e);
            System.exit(1);
        }
    }
}
