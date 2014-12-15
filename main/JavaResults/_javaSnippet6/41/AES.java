package subscription.handler;
import javax.crypto.spec.SecretKeySpec;
 
import javax.crypto.Cipher;
 
public class AES {
static String privateKey = "zigtrakaADMINdef";
 
public static byte[] encrypt(String Message) throws Exception {
Cipher cipher = Cipher.getInstance("AES");
SecretKeySpec key = new SecretKeySpec(privateKey.getBytes("UTF-8"), "AES");
cipher.init(Cipher.ENCRYPT_MODE, key);
return cipher.doFinal(Message.getBytes("UTF-8"));
}
 
public static String decrypt(byte[] cipherText) throws Exception{
Cipher cipher = Cipher.getInstance("AES");
SecretKeySpec key = new SecretKeySpec(privateKey.getBytes("UTF-8"), "AES");
cipher.init(Cipher.DECRYPT_MODE, key);
return new String(cipher.doFinal(cipherText),"UTF-8");
}

public static String bin2hex(byte[] inarray) {
	// TODO Auto-generated method stub
	// parsing tagid to hex...
	int i, j, in;
	String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
			"B", "C", "D", "E", "F" };
	String out = "";

	for (j = 0; j < inarray.length; ++j) {
		in = (int) inarray[j] & 0xff;
		i = (in >> 4) & 0x0f;
		out += hex[i];
		i = in & 0x0f;
		out += hex[i];
	}
	return out;
}
}