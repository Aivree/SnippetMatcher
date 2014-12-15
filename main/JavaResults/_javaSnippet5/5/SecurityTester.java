package org.matrix.security;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

/**
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2013-12-4 下午1:46:14
 */
public class SecurityTester {

	public static void main0(String[] args) {
		String plain_password = "你好";
		String pw_hash = BCrypt.hashpw(plain_password, BCrypt.gensalt());
		System.out.println("pw_hash : " + pw_hash);
	}

	public static void main01(String[] args) {
		String password = "ruoogle.nova";

		String salt = Long.toHexString(10240L);

		System.out.println(salt);
		TextEncryptor encryptor = Encryptors.text(password, salt);

		String text = "你好啊！！！";
		System.out.println(encryptor.encrypt(text));
		System.out.println(encryptor.decrypt(encryptor.encrypt(text)));
	}

	public static void main02(String[] args) {
		String text = "你好啊！！！";
		String encoded = encode(text);
		String decode = decode(encoded);
		System.out.println("encoded : " + encoded);
		System.out.println("decode : " + decode);
	}

	private static final Charset CHARSET = Charset.forName("UTF-8");

	public static String encode(CharSequence string) {
		try {
			ByteBuffer bytes = CHARSET.newEncoder().encode(CharBuffer.wrap(string));

			byte[] bytesCopy = new byte[bytes.limit() + 4];

			System.arraycopy(bytes.array(), 0, bytesCopy, 4, bytes.limit());

			bytesCopy[0] = 127;
			bytesCopy[1] = 127;
			bytesCopy[2] = 127;
			bytesCopy[3] = 127;

			return new String(bytesCopy, CHARSET);
		} catch (CharacterCodingException e) {
			throw new IllegalArgumentException("Encoding failed", e);
		}
	}

	public static String decode(String encode) {
		try {
			byte[] bytes = encode.getBytes(CHARSET);

			byte[] bytesCopy = new byte[bytes.length - 4];

			System.arraycopy(bytes, 4, bytesCopy, 0, bytesCopy.length);
			return CHARSET.newDecoder().decode(ByteBuffer.wrap(bytesCopy)).toString();
		} catch (CharacterCodingException e) {
			throw new IllegalArgumentException("Decoding failed", e);
		}
	}

	public static void main233(String[] args) {
		String password = "123456";
		String salt = "1024";
		BytesEncryptor bytesEncryptor = Encryptors.standard(password, KeyGenerators.string().generateKey());
		
		System.out.println(bytesEncryptor);
		
		String text = "你好啊！！！";

		String encode = new String(bytesEncryptor.encrypt(text.getBytes(CHARSET)), CHARSET);

		System.out.println("encode : " + encode);

		String decode = new String(bytesEncryptor.decrypt(encode.getBytes(CHARSET)), CHARSET);

		System.out.println("decode : " + decode);
	}
	
	public static void main(String[] args) {
		//加密     
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();     
        textEncryptor.setPassword("password");    
        String newPassword = textEncryptor.encrypt("123456");    
        System.out.println(newPassword);    
//        解密     
        BasicTextEncryptor textEncryptor2 = new BasicTextEncryptor();     
        textEncryptor2.setPassword("password");     
        String oldPassword = textEncryptor2.decrypt(newPassword);       
        System.out.println(oldPassword);    
        System.out.println("--------------------------"); 
        
//        StrongTextEncryptor ste = new StrongTextEncryptor();  
//        //加密  
//        ste.setPassword("password");  
//        String encyptedResult= ste.encrypt("123456");  
//        System.out.println("encyptedResult:"+encyptedResult);  
//        //解密  
//        String dencyptedResult = ste.decrypt(encyptedResult);  
//        System.out.println(dencyptedResult); 
	}
}
