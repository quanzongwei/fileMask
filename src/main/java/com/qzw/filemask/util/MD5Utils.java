package com.qzw.filemask.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类(MD5加密后得到16字节的数据)
 * @author quanzongwei
 * @date 2020/1/9
 */
public class MD5Utils {
    /**
     * Md5加密后的数据
     * @return 16字节的数据
     */
    public static byte[] getMd5Bytes(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return messageDigest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
