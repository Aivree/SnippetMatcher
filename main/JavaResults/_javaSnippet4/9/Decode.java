
package server_projekt;

import java.security.Key;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.*;


public class Decode {
    private final static Logger LOGGER = Logger.getLogger(RunServer.class.getName());
    private final static String ALGORITHM ="AES";
    private final static int ITERATION_COUNT = 20;
    private final static byte[] SALT = "9ak12av8wk6zU8d4".getBytes();
    
    public String decode(String text) {
     String dPass = text;
        try {    
            Key key = new SecretKeySpec(SALT, ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key); 
             String dValue = null;
        for (int i = 0; i < ITERATION_COUNT; i++) {
            byte[] decordedValue = new BASE64Decoder().decodeBuffer(dPass);
            byte[] decValue = cipher.doFinal(decordedValue);
            dValue = new String(decValue).substring(SALT.length);
            dPass = dValue;
        }
           
            return dPass;
        } catch (Exception e) {
            LOGGER.severe("Wystapil krytyczny blad DESZYFROWANIA hasla klienta!!");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    private Cipher createCipher(int mode, String plainPassword, byte[] salt)
            throws Exception {
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(plainPassword.toCharArray());
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("AES");
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, secretKey, pbeParamSpec);
        return cipher;
    }
}
