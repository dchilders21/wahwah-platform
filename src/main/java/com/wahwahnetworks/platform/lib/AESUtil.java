package com.wahwahnetworks.platform.lib;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * Created by jhaygood on 3/3/15.
 */
public class AESUtil {
    public static String encrypt(String keyString, byte[] ivData, String clearText) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(ivData);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] key = md.digest(keyString.getBytes("UTF-8"));

        SecretKey secretKey = new SecretKeySpec(key,"AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptedText = cipher.doFinal(clearText.getBytes("UTF-8"));
        String base64EncodedText = Base64.encodeBase64String(encryptedText);
        return base64EncodedText;
    }

    public static String decrypt(String keyString, byte[] ivData, String base64EncodedText) throws Exception {

        byte[] encryptedText = Base64.decodeBase64(base64EncodedText);

        IvParameterSpec iv = new IvParameterSpec(ivData);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] key = md.digest(keyString.getBytes("UTF-8"));

        SecretKey secretKey = new SecretKeySpec(key, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] tokenBytes = cipher.doFinal(encryptedText);
        String clearText = new String(tokenBytes, "UTF-8");

        return clearText;
    }
}
