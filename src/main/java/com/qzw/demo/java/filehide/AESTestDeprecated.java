package com.qzw.demo.java.filehide;

import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.SecureRandom;

public class AESTestDeprecated {

    //    private static String password = "xiehuaxin";
    private static String password = "中国";

    public static void main(String[] args) {
        jdkAES();

    }

    public static void jdkAES() {
        try {
            //1.生成KEY
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] byteKey = secretKey.getEncoded();

            //2.转换KEY
            Key key = new SecretKeySpec(byteKey, "AES");

            //3.加密
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(password.getBytes());
            System.out.println("加密后：" + new String(result, "UTF-8"));

            //4.解密
            cipher.init(Cipher.DECRYPT_MODE, key);
            result = cipher.doFinal(result);
            System.out.println("解密后：" + new String(result, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}