package com.qzw.filemask.interfaces;

import com.qzw.filemask.component.PasswordHolder;
import com.qzw.filemask.util.MD5Utils;

/**
 * 密码处理
 * @author quanzongwei
 * @date 2020/1/18
 */
public interface PasswordHandler {
    /**
     * 获取用户密码
     */
    default String getPassword() {
        return PasswordHolder.password;
    }

    /**
     * 生成xor加密的秘钥
     *
     * @return 16 byte value
     */
    default byte[] get32byteMd5Value() {
        byte[] bytes32 = new byte[32];
        byte[] md5Bytes1 = MD5Utils.getMd5Bytes(getPassword()+2);
        byte[] md5Bytes2 = MD5Utils.getMd5Bytes(getPassword()+3);
        System.arraycopy(md5Bytes1,0,bytes32,0,16);
        System.arraycopy(md5Bytes2,0,bytes32,16,16);
        return bytes32;
    }

    /**
     * 登录时校验用户身份
     * @return 16 byte value
     */
    default byte[] getMd5() {
        byte[] md5Bytes1 = MD5Utils.getMd5Bytes(getPassword());
        return md5Bytes1;
    }

    /**
     * 加密解密文件时候校验用户身份
     * @return 16 byte value
     */
    default byte[] getMd51() {
        byte[] md5Bytes1 = MD5Utils.getMd5Bytes(getPassword()+1);
        return md5Bytes1;
    }
}
