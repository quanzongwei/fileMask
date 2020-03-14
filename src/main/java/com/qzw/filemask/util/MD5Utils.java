package com.qzw.filemask.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * MD5是16字节的数据
 * base64的长度和原长度成正比,但是会多一些
 *
 * @author quanzongwei
 * @date 2020/1/9
 */
public class MD5Utils {


    public static String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            System.out.println(messageDigest.length);
            System.out.println(Base64.getEncoder().encodeToString(messageDigest));
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Md5加密后的数据: 16字节
     */
    public static byte[] getMd5Bytes(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return messageDigest;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Md5加密后的数据: 16字节
     */
    public static byte[] getMd5BytesWithByteInput(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input);
            return messageDigest;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String args[]) throws NoSuchAlgorithmException {
        String s = "GeeksForGeeksadas dasadasd2";
        System.out.println("Your HashCode Generated by MD5 is: " + getMd5(s));
    }
}
