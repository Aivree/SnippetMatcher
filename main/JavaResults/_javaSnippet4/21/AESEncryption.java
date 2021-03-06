import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class AESEncryption {

	private SecureRandom random = new SecureRandom();
	private Base64.Encoder encoder = Base64.getEncoder();
	private Base64.Decoder decoder = Base64.getDecoder();
	private Cipher cipher = null;
    private ByteArrayOutputStream outputStream;
    
    /**
     * Initializes AES Encryption and sets Cipher transformation
     */
	public AESEncryption(){
		try{
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates a random salt used to generate a key in encrypt()
	 * 
	 * @return The random salt
	 */
	public String generateSalt(){
		byte bytes[] = new byte[16];
	    random.nextBytes(bytes);
	    return new String(bytes);
	}
	
	private byte[] generateIv(){
		byte bytes[] = new byte[16];
		random.nextBytes(bytes);
		return bytes;
	}
	
	/**
	 * Encrypt the user supplied word
	 * 
	 * @param word The user supplied word
	 * @param salt The salt generated by generateSalt()
	 * @return The encrypted word with IV appended encoded
	 */
	public String encrypt(String word, String salt) throws Exception{
		
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec("12345asdqwe".toCharArray(), salt.getBytes("UTF-8"), 65536, 128);
		SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
        
        byte[] ivBytes = this.generateIv();
        
	    cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(ivBytes));

	    byte[] encrypted = cipher.doFinal(word.getBytes("UTF-8"));
	    outputStream = new ByteArrayOutputStream();
	    outputStream.write(ivBytes);
	    outputStream.write(encrypted);
		return encoder.encodeToString(outputStream.toByteArray());
	}
	
	/**
	 * Decrypts the encrypted word with the salt and appended IV used to encrypt the word
	 * 
	 * @param encryptedWord The encrypted word returned from encrypt()
	 * @param salt The salt used to encrypt the encrypted word
	 * @return The decrypted word
	 */
	public String decrypt(String encryptedWord, String salt) throws Exception{
		
		byte[] decodedBytes = decoder.decode(encryptedWord);
		
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec("12345asdqwe".toCharArray(), salt.getBytes("UTF-8"), 65536, 128);
        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
		
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(decodedBytes, 0, 16));
        
		return new String(cipher.doFinal(decodedBytes, 16, decodedBytes.length - 16));
	}
	
}
