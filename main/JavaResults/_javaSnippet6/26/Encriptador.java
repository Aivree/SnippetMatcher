package app.clase4.ejercicio1.practica;

import static app.clase4.ejercicio1.practica.AES.IV;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encriptador {

    final static String palabrasecreta = "1234567812345678";
    final static String encryptionKey = "0123456789abcdef";
    final static String IV = "AAAAAAAAAAAAAAAA";

    //http://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
    public static byte[] CifrarMensaje1(String mensaje) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return cipher.doFinal(mensaje.getBytes("UTF-8"));        
    }

    public static String DescifrarMensaje1(byte[] mensaje) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return new String(cipher.doFinal(mensaje), "UTF-8");
    }

    public static String CifrarMensaje(String mensaje) throws Exception {

        byte[] mensajeBytes = palabrasecreta.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(mensajeBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); //"AES/CBC/PKCS5Padding"
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encrypted = cipher.doFinal(mensaje.getBytes());

        return new String(encrypted);
    }

    public static String DescifrarMensaje(String mensaje) throws Exception {

        byte[] mensajeBytes = palabrasecreta.getBytes();
        SecretKeySpec secretKeySpec = new SecretKeySpec(mensajeBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decrypted = cipher.doFinal(mensaje.getBytes());
        return new String(decrypted);
    }
}
