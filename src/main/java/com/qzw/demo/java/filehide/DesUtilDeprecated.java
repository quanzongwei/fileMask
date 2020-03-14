package com.qzw.demo.java.filehide;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * des 加密数量是向上取蒸3,同时是8的倍数, 注意
 * 有各种问题啊, 比如密码不对: https://stackoverflow.com/questions/8049872/given-final-block-not-properly-padded
 *
 * @author BG388892
 * @date 2020/1/9
 */
public class DesUtilDeprecated {
    public static void main(String[] argv) {
        try {
//            KeyGenerator keygenerator = KeyGenerator.getInstance("DES");
//            SecretKey myDesKey = keygenerator.generateKey();
            String pass = "Bar12345";
            String pass2 = "Bar12346";
            System.out.println(pass.getBytes().length);
            SecretKey myDesKey = new SecretKeySpec(pass.getBytes(), "DES");
            SecretKey myDesKey2 = new SecretKeySpec(pass2.getBytes(), "DES");
            Cipher desCipher;
            // Create the cipher /后面是模式和padding
            desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
            //sensitive information
//            byte[] text = "No body can see me".getBytes();
            byte[] text = "10000000000001111".getBytes();
            System.out.println(text.length);
            System.out.println("Text [Byte Format] : " + text);
            System.out.println("Text : " + new String(text));
            // Encrypt the text
            byte[] textEncrypted = desCipher.doFinal(text);
            System.out.println(textEncrypted.length);
//            System.out.println("Text Encryted : " + textEncrypted);
            System.out.println("Text Encryted : " + ByteUtil.byteToHex(textEncrypted));
            // Initialize the same cipher for decryption
            desCipher.init(Cipher.DECRYPT_MODE, myDesKey2);
            // Decrypt the text
            byte[] textDecrypted = desCipher.doFinal(textEncrypted);
            System.out.println("Text Decryted : " + new String(textDecrypted));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }


    public static String enrypt(String text, String pass) {


        return null;
    }
}