package coupon.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * 暗号化複合化ユーティリティー
 * @author B00829
 *
 */
public final class CryptUtils {

	/** コンストラクタ（生成不可）*/
    private CryptUtils(){}

    /** デフォルトキー：masayuki-satoshi */
    private static final String KEY_DEFALT = "masayuki-satoshi";

    /**
     * 暗号化(デフォルトキー)
     * @param data 暗号化したい文字列
     * @return
     * @throws Exception
     */
	public static String encrypt(String data) throws Exception {
		SecretKeySpec sksSpec = new SecretKeySpec(KEY_DEFALT.getBytes(), "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
		char[] encodeCharArray = Hex.encodeHex(cipher.doFinal(data.getBytes()));
		return String.valueOf(encodeCharArray);
	}

	/**
	 * 複合化(デフォルトキー)
	 * @param data 複合化したい文字列
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String data) throws Exception {
		byte[] bytes = Hex.decodeHex(data.toCharArray());
		SecretKeySpec sksSpec = new SecretKeySpec(KEY_DEFALT.getBytes(), "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, sksSpec);
		byte[] decrypted = cipher.doFinal(bytes);
		return new String(decrypted);
	}

    /**
     * 暗号化
     * @param key 暗号化キー
     * @param data 暗号化したい文字列
     * @return
     * @throws Exception
     */
	public static String encrypt(String key, String data) throws Exception {
		SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(), "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(Cipher.ENCRYPT_MODE, sksSpec);
		char[] encodeCharArray = Hex.encodeHex(cipher.doFinal(data.getBytes()));
		return String.valueOf(encodeCharArray);
	}

	/**
	 * 複合化
	 * @param key 複合化キー
	 * @param data 複合化したい文字列
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String key, String data) throws Exception {
		byte[] bytes = Hex.decodeHex(data.toCharArray());
		SecretKeySpec sksSpec = new SecretKeySpec(key.getBytes(), "Blowfish");
		Cipher cipher = Cipher.getInstance("Blowfish");
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, sksSpec);
		byte[] decrypted = cipher.doFinal(bytes);
		return new String(decrypted);
	}
}
