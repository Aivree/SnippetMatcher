package generator.podotree.com;

import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SimpleEncryptor {
	public static final String DEFAULT_KEYGEN_ALGORITHM = "PBEWITHSHAAND256BITAES-CBC-BC";
	public static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String UTF8 = "UTF-8";
    private static final byte[] IV =
    	{ 10, 2, 3, -4, 20, 73, 47, -38, 27, -22, 11, -20, -22, 37, 36, 54 };
    
    private static final char[] keyValue = 
		new char[] { 48, 66, 84, 52, 49, 100, 120, 54, 111, 55, 80, 73, 56, 52, 77, 79 };

    private Cipher encryptor;
    private Cipher decryptor;
    
    	
    public SimpleEncryptor(byte[] salt) {
    	this(DEFAULT_KEYGEN_ALGORITHM, DEFAULT_CIPHER_ALGORITHM, salt);
    }

    	
    public SimpleEncryptor(String keygenAlgorithm, String cipherAlgorithm, byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(keygenAlgorithm);
            KeySpec keySpec = new PBEKeySpec(keyValue, salt, 2, 256);
            SecretKey tmp = factory.generateSecret(keySpec);
            
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            
            encryptor = Cipher.getInstance(cipherAlgorithm);
            encryptor.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(IV));
            decryptor = Cipher.getInstance(cipherAlgorithm);
            decryptor.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
//    public String getUserKey(byte[] salt) {
//        try {
//            SecretKeyFactory factory = SecretKeyFactory.getInstance();
//            KeySpec keySpec = new PBEKeySpec(keyValue, salt, 2, 256);
//            SecretKey tmp = factory.generateSecret(keySpec);
//            
//            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
//            
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
//        
//        return secret 
//    }

    public String encrypt(String source) {
        if (source == null) {
            return null;
        }
        try {
            return new String(Base64.encode(encryptor.doFinal((source).getBytes(UTF8))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String source) {
        if (source == null) {
            return null;
        }
        try {
            return new String(decryptor.doFinal(Base64.decode(source)), UTF8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
