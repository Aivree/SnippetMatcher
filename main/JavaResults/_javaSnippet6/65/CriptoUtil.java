package org.commonjava.cripto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Jean
 */
public final class CriptoUtil {

	private static final String CIPHER = "Blowfish";

	public static final String cript(String str, String chave) {
		String strCript = str;
		try {
			Cipher ch = Cipher.getInstance(CIPHER);
			SecretKey k1 = new SecretKeySpec(chave.getBytes(), CIPHER);

			// criptografando
			ch.init(Cipher.ENCRYPT_MODE, k1);
			byte b[] = ch.doFinal(strCript.getBytes());
			String s1 = Conversion.byteArrayToBase64String(b);
			strCript = s1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strCript;
	}
	
	public static final byte[] cript(byte[] str, String chave) {
		try {
			SecretKey key = new SecretKeySpec(chave.getBytes(), CIPHER);
			Cipher ch = Cipher.getInstance(CIPHER);
			ch.init(Cipher.ENCRYPT_MODE, key);
			return ch.doFinal(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static final byte[] decript(byte[] str, String chave) {
		try {
			SecretKey key = new SecretKeySpec(chave.getBytes(), CIPHER);
			Cipher ch = Cipher.getInstance(CIPHER);
			ch.init(Cipher.DECRYPT_MODE, key);
			return ch.doFinal(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	public static final String decript(String str, String chave) {
		String strDecript = str;
		try {
			Cipher ch = Cipher.getInstance(CIPHER);
			SecretKey k1 = new SecretKeySpec(chave.getBytes(), CIPHER);

			// decriptografando
			ch.init(Cipher.DECRYPT_MODE, k1);
			byte b[] = Conversion.base64StringToByteArray(strDecript);
			byte b1[] = ch.doFinal(b);
			String s1 = new String(b1);
			strDecript = s1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strDecript;
	}

	public static String md5(String value) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md.update(value.getBytes());
		BigInteger hash = new BigInteger(1, md.digest());
		String retornaSenha = hash.toString(16);
		return retornaSenha;

	}

}