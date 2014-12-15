

package parqueo;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Core @ i3 Intel
 */
public class Encriptacion {
       
    public String encriptar(String text) {
      try {
         String key = "Leer2t45Bar12345"; // 128 bit llave

         // Crear llave y codigo

         Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
         Cipher cipher = Cipher.getInstance("AES");

         // encriptar el text

         cipher.init(Cipher.ENCRYPT_MODE, aesKey);
         byte[] encrypted = cipher.doFinal(text.getBytes());
         String result= new String(encrypted);

         // decriptar el text
         //cipher.init(Cipher.DECRYPT_MODE, aesKey);
         //String decrypted = new String(cipher.doFinal(encrypted));
         //System.err.println(decrypted);
         return result;

      }catch(Exception e) {

         e.printStackTrace();
         return "%%%%%%%%%%";
      }
    }
    
}
