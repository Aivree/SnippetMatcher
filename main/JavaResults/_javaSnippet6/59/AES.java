package org.xhome.cmms.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * @project crypto
 * @author jhat
 * @email cpf624@126.com
 * @date Jan 29, 20136:35:52 PM
 */
public class AES {

	private Cipher cipher;
	private IvParameterSpec iv;
	private SecretKeySpec spec;
	private String key;
	
	public AES(String key){
		this.key = key;
		try{
			cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
			iv=new IvParameterSpec("0102030405060708".getBytes());
			spec=new SecretKeySpec(key.getBytes(), "AES");
		}catch (Exception e) {
		}
	}
	
	public String encrypt(String data) {
		try{
			cipher.init(Cipher.ENCRYPT_MODE, spec,iv);
			return new String(Base64.encodeBase64(cipher.doFinal(data.getBytes())));
		}catch (Exception e) {
		}
		return null;
		
	}

	public String decrypt(String data) {
		try{
			cipher.init(Cipher.DECRYPT_MODE, spec,iv);
			return new String(cipher.doFinal(Base64.decodeBase64(data.getBytes())));
		}catch (Exception e) {
		}
		return null;
	}
	
	public String getKey(){
		return key;
	}

}
