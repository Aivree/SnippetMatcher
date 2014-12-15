import org.junit.Test;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class SymmetricPasswordEncryptionExample {


    @Test
    public void encryptPasswordSymmetrically() {

        // enable unlimited strength 
        // pls use Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files instead!
        try {
            Class<?> securityClass = java.lang.Class.forName("javax.crypto.JceSecurity");
            Field restrictedField = securityClass.getDeclaredField("isRestricted");
            restrictedField.setAccessible(true);
            restrictedField.set(null, false);
        } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Disable the crypto restriction programmatically faild!", e);
        }

        TextEncryptor passwordEncryptor = Encryptors.text("password", KeyGenerators.string().generateKey());

        String origPassword = "plaintextPassword";
        
        String encryptedPassword = passwordEncryptor.encrypt(origPassword);
        String decryptedpassword = passwordEncryptor.decrypt(encryptedPassword);

        assertEquals(origPassword, decryptedpassword);
    }
}
