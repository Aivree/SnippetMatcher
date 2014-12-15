package com.google.zxing.client.android.utils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class PasswordCheck {

	static String algorithm = "DESede";

	static String mode = "CBC";

	static String padding = "PKCS5Padding";

	static String key = "Sa#qk5usfmMI-@2dbZP9`jL3";

	static String spec = "beLd7$lB";

	static String modulus = "";

	static int padLen = 8;

	private static Cipher cipherEncrypt = null;

	static SecretKey secretKey = null;

	static IvParameterSpec ivSpec = null;

	static PBEParameterSpec pbeParamSpec = null;

	static SecretKeySpec secretkeySpec = null;

	static PublicKey publicKey = null;

	static String transformation = null;

	static PrivateKey privateKey = null;

	static boolean nonSunProviders = false;

	static Provider providerClass = null;

	/**
	 * 校验密码是否正确
	 * 
	 * @param loginPass
	 *            用户登录密码
	 * @param savePass
	 *            服务器/数据库返回密码
	 * @return true/false
	 * @throws Exception
	 */
	public static boolean checkPass(String loginPass, String savePass) {
		transformation = algorithm;
		if (mode != null && !mode.equals("") && padding != null
				&& !padding.equals(""))
			transformation = (new StringBuilder()).append(transformation)
					.append("/").append(mode).append("/").append(padding)
					.toString();
		String newResult = null;
		try {
			cipherEncrypt = buildCipher(true);
			newResult = byte2hex(encData(loginPass));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (newResult != null && newResult.length() > 0) {
			if (newResult.equals(savePass)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public synchronized static byte[] encData(String password) throws Exception {
		byte temp[] = password.getBytes();
		temp = pad(temp);
		byte encryptVal[] = null;
		try {
			encryptVal = cipherEncrypt.doFinal(temp);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("234567890-");
		}
		return encryptVal;
	}

	protected static byte[] pad(byte in[]) {
		return pad(in, padLen);
	}

	protected static byte[] pad(byte in[], int padLen) {
		if (padLen == 0)
			return in;
		int inlen = in.length;
		int outlen = inlen;
		int rem = inlen % padLen;
		if (rem > 0)
			outlen = inlen + (padLen - rem);
		byte out[] = new byte[outlen];
		for (int xx = 0; xx < inlen; xx++)
			out[xx] = in[xx];

		return out;
	}

	static Cipher buildCipher(boolean encrypt) throws Exception {
		Cipher cipher = null;
		int cryptMode = !encrypt ? 2 : 1;
		if (algorithm.equals("DESede") || algorithm.equals("TripleDES")) {
			if (secretKey == null || ivSpec == null) {
				DESedeKeySpec keyspec = new DESedeKeySpec(key.getBytes());
				SecretKeyFactory factory = SecretKeyFactory
						.getInstance(algorithm);
				secretKey = factory.generateSecret(keyspec);
				ivSpec = new IvParameterSpec(spec.getBytes());
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			if (transformation.indexOf("ECB") < 0)
				cipher.init(cryptMode, secretKey, ivSpec);
			else
				cipher.init(cryptMode, secretKey);
		} else if (algorithm.equals("DES")) {
			if (secretKey == null || ivSpec == null) {
				DESKeySpec keyspec = new DESKeySpec(key.getBytes());
				SecretKeyFactory factory = SecretKeyFactory
						.getInstance(algorithm);
				secretKey = factory.generateSecret(keyspec);
				ivSpec = new IvParameterSpec(spec.getBytes());
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			if (transformation.indexOf("ECB") < 0)
				cipher.init(cryptMode, secretKey, ivSpec);
			else
				cipher.init(cryptMode, secretKey);
		} else if (algorithm.startsWith("PBEWith")) {
			if (secretKey == null || pbeParamSpec == null) {
				pbeParamSpec = new PBEParameterSpec(spec.getBytes(), 20);
				PBEKeySpec pbeKeySpec = new PBEKeySpec(spec.toCharArray());
				SecretKeyFactory keyFac = SecretKeyFactory
						.getInstance(algorithm);
				secretKey = keyFac.generateSecret(pbeKeySpec);
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			cipher.init(cryptMode, secretKey, pbeParamSpec);
		} else if (algorithm.equals("RSA")) {
			if (publicKey == null || privateKey == null) {
				KeyFactory fac = KeyFactory.getInstance("RSA", providerClass);
				publicKey = fac.generatePublic(new RSAPublicKeySpec(
						new BigInteger(modulus), new BigInteger(key)));
				privateKey = fac.generatePrivate(new RSAPrivateKeySpec(
						new BigInteger(modulus), new BigInteger(spec)));
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			if (encrypt)
				cipher.init(cryptMode, publicKey);
			else
				cipher.init(cryptMode, privateKey);
		} else {
			if (secretkeySpec == null) {
				int padLen = algorithm.equals("SKIPJACK") ? 10 : 16;
				byte byteArray[] = spec.getBytes();
				byteArray = pad(byteArray, padLen);
				secretkeySpec = new SecretKeySpec(byteArray, algorithm);
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			cipher.init(cryptMode, secretkeySpec);
		}
		return cipher;
	}

	public synchronized static String byte2hex(byte[] b) {
		String s = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < b.length; n++) {
			s = Integer.toHexString(b[n] & 0xFF);
			sb.append((s.length() == 1) ? "0" + s : s);
		}
		return sb.toString().toUpperCase().trim();
	}
}
