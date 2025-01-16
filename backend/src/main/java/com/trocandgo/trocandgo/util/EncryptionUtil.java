package com.trocandgo.trocandgo.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class EncryptionUtil {

    private final SecretKey secretKey;

    public EncryptionUtil(String encryptionKey) {
        if (encryptionKey.length() != 16) {
            throw new IllegalArgumentException("Invalid AES key length: " + encryptionKey.length() + " characters. Expected: 16");
        }
        this.secretKey = new SecretKeySpec(encryptionKey.getBytes(), "AES");
    }

    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
}
