package com.caffee.utils;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

public final class CryptoUtils {
    private static final String PASSWORD = "CafeByKir";

    public static String crypt(Object obj, String salt) {
        TextEncryptor encryptor = Encryptors.text(PASSWORD, salt);
        return encryptor.encrypt(obj.toString());
    }

    public static String doSalt () {
        return KeyGenerators.string().generateKey();
    }

    public static String decrypt(String encryptedText, String salt) {
        TextEncryptor decryptor = Encryptors.text(PASSWORD, salt);
        return decryptor.decrypt(encryptedText);
    }
}
