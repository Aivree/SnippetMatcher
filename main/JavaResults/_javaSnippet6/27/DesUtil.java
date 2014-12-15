package com.dream.utils;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import com.dream.constants.Constant;

public class DesUtil {
	private static Key key;

	// 根据参数生成KEY
	static {
		try {
			KeyGenerator _generator = KeyGenerator.getInstance("DES");
			_generator.init(new SecureRandom(Constant.DES_ENCRIPT_KEY.getBytes()));
			key = _generator.generateKey();
			_generator = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 加密明文输入,密文输出
	public static byte[] getEncCode(String str) throws Exception {
		byte[] byteFina = null;
		Cipher cipher;
		cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byteFina = cipher.doFinal(str.getBytes());
		return byteFina;
	}

	// 解密以byte[]密文输入,以byte[]明文输出
	public static byte[] getDesCode(byte[] byteD) throws Exception {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cipher = null;
		}
		return byteFina;
	}
	
	
}
