import javax.crypto.*;
import java.security.*;

public class Demo {
	public static void main(String[] args) throws Exception {
		String algo = "DES";
		SecretKey key = KeyGenerator.getInstance(algo).generateKey();
		Cipher cipher = Cipher.getInstance(algo);
		String plain = "hello world", chr = "UTF8";
		System.out.printf("Original text: %s\n", plain);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] tmp = cipher.doFinal(plain.getBytes(chr));
		String str = new sun.misc.BASE64Encoder().encode(tmp);
		System.out.printf("Encoded text: %s\n", str);
		cipher.init(Cipher.DECRYPT_MODE, key);
		tmp = new sun.misc.BASE64Decoder().decodeBuffer(str);
		tmp = cipher.doFinal(tmp);
		str = new String(tmp, chr);
		System.out.printf("Decoded text: %s\n", str);
	}
}