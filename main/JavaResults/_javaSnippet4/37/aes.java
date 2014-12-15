import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
 
class EncryptorDecryptor {
 
  public static String bin2hex(byte[] data)
  {
    if (data==null)
    {
      return null;
    }
 
    int len = data.length;
    String str = "";
    for (int i=0; i<len; i++) {
      if ((data[i]&0xFF)<16)
        str = str + "0" + java.lang.Integer.toHexString(data[i]&0xFF);
      else
        str = str + java.lang.Integer.toHexString(data[i]&0xFF);
    }
    return str;
  }
 
  public static byte[] hex2bin(String str) {
    if (str==null) {
      return null;
    } else if (str.length() < 2) {
      return null;
    } else {
      int len = str.length() / 2;
      byte[] buffer = new byte[len];
      for (int i=0; i<len; i++) {
        buffer[i] = (byte) Integer.parseInt(str.substring(i*2,i*2+2),16);
      }
      return buffer;
    }
  }
 
  public static String encrypt(String salt, String iv, String plaintext, String pwd) throws Exception {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    KeySpec spec = new PBEKeySpec(pwd.toCharArray(), hex2bin(salt), 1024, 256);
    SecretKey tmp = factory.generateSecret(spec);
    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
 
    byte[] iv_bytes = hex2bin(iv);
    String value = new String(iv_bytes);
    IvParameterSpec ivspec = new IvParameterSpec(iv_bytes);
 
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secret, ivspec);
 
    byte[] encrypted = cipher.doFinal(plaintext.getBytes());
 
    return new String(bin2hex(encrypted));
  }
 
  public static String decrypt(String salt, String iv, String payload, String pwd) throws Exception {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    KeySpec spec = new PBEKeySpec(pwd.toCharArray(), hex2bin(salt), 1024, 256);
    SecretKey tmp = factory.generateSecret(spec);
    SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
 
    byte[] iv_bytes = hex2bin(iv);
    String value = new String(iv_bytes);
    IvParameterSpec ivspec = new IvParameterSpec(iv_bytes);
 
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, secret, ivspec);
 
    byte[] payload_bytes = hex2bin(payload);
    byte[] decrypted = cipher.doFinal(payload_bytes);
 
    return new String(decrypted);
  }
}
