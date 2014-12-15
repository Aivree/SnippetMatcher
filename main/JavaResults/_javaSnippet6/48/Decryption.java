import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import sun.misc.BASE64Encoder;
 
public class Decryption
{
public static void main(String args[])
{
    new Decryption().encrypt();
}
void encrypt()
{
    try
    {
    String plainData="hello",cipherText,decryptedText;
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(128);
    SecretKey secretKey = keyGen.generateKey();
    Cipher aesCipher = Cipher.getInstance("AES");
    aesCipher.init(Cipher.ENCRYPT_MODE,secretKey);
    byte[] byteDataToEncrypt = plainData.getBytes();
    byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
    cipherText = new BASE64Encoder().encode(byteCipherText);
    aesCipher.init(Cipher.DECRYPT_MODE,secretKey,aesCipher.getParameters());
    byte[] byteDecryptedText = aesCipher.doFinal(byteCipherText);
    decryptedText = new String(byteDecryptedText);
    System.out.println(" \n Cipher Data : "+cipherText+" \n Decrypted Data : "+decryptedText);
    }
    catch(Exception e)
    {
         
    }
}
}


