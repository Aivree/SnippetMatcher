/**
 * 
 */
package org.sen.security.service.encryption;

import java.security.spec.KeySpec;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * @author Sen
 * 
 * DSA - Digital Signature Algorithm
 * DES - Data Encryption Standard  
 * AES - Advanced Encryption Standard.
 * PBE - Password-Based Encryption
 * ECB - Electronic Codebook mode
 * PKCS5-style padding is "PKCS5Padding"
 * AES encryption algorithm with 128-bit key, ECB mode and PKCS7 padding
 * 
 * refer : 
 * 
 * <link><a>http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/CryptoSpec.html#Examples</a></link>
 * 
 * 
 * 		**** OPERATIONS ****
 * 	1. generating a key, 
 *  2. creating and initializing a cipher object, 
 *  3. encrypting a file, 
 *  4. and then decrypting it.
 * 
 */
public class SecretKeyGenerator {
	
	private static final String ALGORITHM = "AES";
	
	public SecretKeyGenerator() {

	}
	
	/**
	 *  Takes the password and return the SecretKey.
	 *  
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public SecretKey generatePBEKey(String password) throws Exception {
		PBEKeySpec pbeKeySpec = null;
        PBEParameterSpec pbeParamSpec = null;
        SecretKeyFactory keyFac = null;

        // Salt
        byte[] salt = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
        };

        // Iteration count
        int count = 20;

        // Create PBE parameter set
        pbeParamSpec = new PBEParameterSpec(salt, count);

        pbeKeySpec = new PBEKeySpec(password.toCharArray());
        keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);     
        //SecretKey skeySpec = new SecretKeySpec(passByte, "AES");
        
        return pbeKey;
        
    }	

	/**
	 * Returns the default AES 128bit default SecretKey.
	 *  
	 * @return
	 * @throws Exception
	 */
	public SecretKey generateAESDefaultKey() throws Exception {		
		KeyGenerator keyGenS = KeyGenerator.getInstance(ALGORITHM);
		keyGenS.init(128);		
		SecretKey secretKey = keyGenS.generateKey();
		System.out.println("generateAESDefaultKey() - Secret Key is generated. ->  "+new String(secretKey.getEncoded()));
		return secretKey;		
	}

	
	
	//TESTING
	public void generatePBEKey() throws Exception {
		
		PBEKeySpec pbeKeySpec = null;
        PBEParameterSpec pbeParamSpec = null;
        SecretKeyFactory keyFac = null;

        // Salt
        byte[] salt = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
        };

        // Iteration count
        int count = 20;

        // Create PBE parameter set
        pbeParamSpec = new PBEParameterSpec(salt, count);

        // Prompt user for encryption password.
        String password = "metturssdelta77";        
        pbeKeySpec = new PBEKeySpec(password.toCharArray());
        keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

        // Our cleartext
        String cleartextStr = "This is another example";
        System.out.println("cleartextStr - "+cleartextStr);
        byte[] cleartext = cleartextStr.getBytes();

        // Encrypt the cleartext
        byte[] ciphertext = pbeCipher.doFinal(cleartext);
        
        System.out.println("ciphertext - "+new String(ciphertext));
        
        System.out.println("-------------------- desc ---------------");
        password = "metturssdelta77";
        pbeKeySpec = new PBEKeySpec(password.toCharArray());
        keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        pbeKey = keyFac.generateSecret(pbeKeySpec);

        
        //Decrypt the ciphertext
        pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
        byte[] orgtext = pbeCipher.doFinal(ciphertext);
        System.out.println("orgtext - "+new String(orgtext));
        
	}
	
	//TESTING
	public void generateUserKey() throws Exception {
		
		KeyGenerator keyGenS = KeyGenerator.getInstance(ALGORITHM);
		keyGenS.init(128);		
		SecretKey secretKey = keyGenS.generateKey();
		System.out.println("generateUserKey() - Secret Key is generated. ->  "+new String(secretKey.getEncoded()));
    
		String passString = "password";
		byte[] passByte = passString.getBytes();
		SecretKey skeySpec = new SecretKeySpec(passByte, "AES");
		System.out.println("generateUserKey() - Secret Key is generated."+skeySpec.getEncoded()+", "+new String(skeySpec.getEncoded()));
		
		/* Derive the key, given password and salt. */
		String password = "password123";
		String salt = "ABKFJJFKF";
		
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		
		/* Encrypt the message. */
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		AlgorithmParameters params = cipher.getParameters();
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] ciphertext = cipher.doFinal("Hello, World!".getBytes("UTF-8"));
		System.out.println("ciphertext  - "+new String(ciphertext));
		
		System.out.println("------------------------------------------");
	}
	
	public static void main( String[] args ) {
		 SecretKeyGenerator kg = new SecretKeyGenerator();
		 try {
			kg.generatePBEKey();
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
}
