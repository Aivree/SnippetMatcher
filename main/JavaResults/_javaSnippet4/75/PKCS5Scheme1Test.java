/****************************************************************
 * Licensed to the AOS Community (AOS) under one or more        *
 * contributor license agreements.  See the NOTICE file         *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The AOS licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/
package io.aos.crypto.spl03;

import java.security.MessageDigest;

import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Basic test of the PKCS #5 Scheme 1 implementation.
 */
public class PKCS5Scheme1Test
{
    public static void main(
        String[] args)
        throws Exception
    {
        char[] password = "hello".toCharArray();
        byte[] salt = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
        byte[] input = new byte[] { 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
        int    iterationCount = 100;
        
        System.out.println("input  : " + Utils.toHex(input));
        
        // encryption step using regular PBE
        Cipher           cipher = Cipher.getInstance("PBEWithSHA1AndDES","BC");
        SecretKeyFactory fact = SecretKeyFactory.getInstance("PBEWithSHA1AndDES", "BC");
        PBEKeySpec       pbeKeySpec = new PBEKeySpec(password, salt, iterationCount);
        
        cipher.init(Cipher.ENCRYPT_MODE, fact.generateSecret(pbeKeySpec));
        
        byte[] enc = cipher.doFinal(input);
        
        System.out.println("encrypt: " + Utils.toHex(enc));
        
        // decryption step - using the local implementation
        cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        
        PKCS5Scheme1 pkcs5s1 = new PKCS5Scheme1(MessageDigest.getInstance("SHA-1", "BC"));
        
        byte[] derivedKey = pkcs5s1.generateDerivedKey(password, salt, iterationCount);
        
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(derivedKey, 0, 8, "DES"), new IvParameterSpec(derivedKey, 8, 8));
        
        byte[] dec = cipher.doFinal(enc);
        
        System.out.println("decrypt: " + Utils.toHex(dec));
    }
}
