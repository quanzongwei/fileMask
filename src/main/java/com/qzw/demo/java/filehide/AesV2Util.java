package com.qzw.demo.java.filehide;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;

/**
 * 已测试可用
 *
 * @author BG388892
 * @date 2020/1/9
 */
public class AesV2Util {


    public static String encrypt(String strToEncrypt, String secret, String salt) {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }


    public static String decrypt(String strToDecrypt, String secret, String salt) {
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
            throw new RuntimeException(e.toString());
        }
//        return null;
    }
//    private static String secretKey = "boooooooooom!!!";
//    private static String salt = "ssshhhhhhhhhhh!!!!";
//    private static String secretKey2 = "boooooooooom!!!";

    private static String secretKey = "admin";
    private static String salt = "123456";
    private static String secretKey2 = "admin";


    /**
     * 10个耗时2s
     * 100个耗时25s, 耗时太长了, 自己手动简单加密吧
     *
     * @param args
     */
    public static void main(String[] args) {
        String originalString = "howtodoinjava.com";
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {

            String secretKey = randomStr();
            System.out.println(secretKey);
            String salt = randomStr();
            System.out.println(salt);
            String encryptedString = AesV2Util.encrypt(originalString, secretKey, salt);
            String decryptedString = AesV2Util.decrypt(encryptedString, secretKey, salt);
            System.out.println(originalString);
            System.out.println(encryptedString);
            System.out.println(decryptedString);
        }
        System.out.println("耗时" + (System.currentTimeMillis() - t1));


    }

    public static String randomStr() {
        Random random = new Random();
        int randomInt = random.nextInt(100);
        randomInt = randomInt + 1;
        String result = "";
        for (int i = 0; i < randomInt; i++) {
            result += "A";
        }
        return result;
    }
}
