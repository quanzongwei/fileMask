package com.qzw.filemask.service;

import com.qzw.filemask.component.GlobalPasswordHolder;
import com.qzw.filemask.enums.MaskExceptionEnum;
import com.qzw.filemask.exception.MaskException;
import com.qzw.filemask.util.MD5Utils;

/**
 * 密码处理
 * @author quanzongwei
 * @date 2020/1/18
 */
public class PasswordService {
    /**
     * 获取用户密码
     */
    private static String getPassword() {
        String password = GlobalPasswordHolder.getPassword();
        if (password == null) {
            throw new MaskException(MaskExceptionEnum.PASSWORD_NOT_EXISTS);
        }
        return GlobalPasswordHolder.getPassword();
    }


    /**
     * 登录时校验用户身份
     * @return 16 byte value
     */
    public static byte[] getMd5ForLogin() {
        byte[] md5Bytes0 = MD5Utils.getMd5Bytes(getPassword());
        return md5Bytes0;
    }

    /**
     * 加密解密文件时候校验用户身份
     * @return 16 byte value
     */
    public static byte[] getMd51ForFileAuthentication() {
        byte[] md5Bytes1 = MD5Utils.getMd5Bytes(getPassword()+1);
        return md5Bytes1;
    }

    /**
     * 用于生成加密文件内容的秘钥
     *
     * @return 32 byte value
     */
    public static byte[] getMd523ForContentEncrypt() {
        byte[] bytes32 = new byte[32];
        byte[] md5Bytes1 = MD5Utils.getMd5Bytes(getPassword() + 2);
        byte[] md5Bytes2 = MD5Utils.getMd5Bytes(getPassword() + 3);
        System.arraycopy(md5Bytes1, 0, bytes32, 0, 16);
        System.arraycopy(md5Bytes2, 0, bytes32, 16, 16);
        return bytes32;
    }

    /**
     * 用于生成加密uuid字符串的秘钥
     *
     * @return 32 byte value
     */
    public static byte[] getMd545ForUuidEncrypt() {
        byte[] md5Bytes4 = new byte[32];
        byte[] md5Bytes1 = MD5Utils.getMd5Bytes(getPassword() + 4);
        byte[] md5Bytes2 = MD5Utils.getMd5Bytes(getPassword() + 5);
        System.arraycopy(md5Bytes1, 0, md5Bytes4, 0, 16);
        System.arraycopy(md5Bytes2, 0, md5Bytes4, 16, 16);
        return md5Bytes4;
    }
}
