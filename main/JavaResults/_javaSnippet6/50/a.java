/*
 * Decompiled with CFR 0_79.
 */
package com.baidu.location.b.b;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class a {
    private static final String a = "AES";
    private static final String if = "AES/CBC/PKCS5Padding";

    private a() {
    }

    public static byte[] a(String string, String string2, byte[] arrby) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(string2.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, (Key)secretKeySpec, new IvParameterSpec(string.getBytes()));
        return cipher.doFinal(arrby);
    }

    public static byte[] if(String string, String string2, byte[] arrby) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(string2.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, (Key)secretKeySpec, new IvParameterSpec(string.getBytes()));
        return cipher.doFinal(arrby);
    }
}

