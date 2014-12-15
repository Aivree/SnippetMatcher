package cipher;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SymmertricCipher implements ICipher {
	private Key key;
	private Cipher cipher;
	private static final byte[] salt = "dhasidhsjahdkjas hd7as6d786a 78dysa7dy78a"
			.getBytes();

	public SymmertricCipher(String key, String algorithm)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException {
		SecretKey tmp = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
				.generateSecret(
						new PBEKeySpec(key.toCharArray(), salt, 1024, 128));
		
		this.key = new SecretKeySpec(tmp.getEncoded(), algorithm);
		this.cipher = Cipher.getInstance(algorithm);

	}

	@Override
	public byte[] encrypt(byte[] data) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	@Override
	public byte[] decrypt(byte[] data) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(data);
	}
}
