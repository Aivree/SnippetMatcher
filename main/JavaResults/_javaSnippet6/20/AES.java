import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AES {
	
	static String IV = "USBUSBUSBUSBUSBU";
	static String llave = "0123456789abcdef";
	String key;
	
	public AES(){
		this.key = llave;
	}

	public String getKey(){
		return this.key;
	}


	public byte[] cifrar_AES(String texto)
			throws Exception {
		
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(llave.getBytes("UTF-8"),
				"AES");
		cipher.init(Cipher.ENCRYPT_MODE, key,
				new IvParameterSpec(IV.getBytes("UTF-8")));
		return cipher.doFinal(texto.getBytes("UTF-8"));
	}

	public String descifrar_AES(byte[] texto, String llave)
			throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
		SecretKeySpec key = new SecretKeySpec(llave.getBytes("UTF-8"),
				"AES");
		cipher.init(Cipher.DECRYPT_MODE, key,
				new IvParameterSpec(IV.getBytes("UTF-8")));
		return new String(cipher.doFinal(texto), "UTF-8");
	}
}