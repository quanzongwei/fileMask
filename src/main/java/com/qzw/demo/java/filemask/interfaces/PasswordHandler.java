package com.qzw.demo.java.filemask.interfaces;

import com.qzw.demo.java.filehide.MD5Utils;
import com.qzw.demo.java.filemask.component.PasswordHolder;

/**
 * @author BG388892
 * @date 2020/1/18
 */
public interface PasswordHandler {
    default String getPassword() {
        return PasswordHolder.password;
    }

    default byte[] get32byteMd5Value() {
        byte[] bytes32 = new byte[32];
        byte[] md5Bytes1 = MD5Utils.getMd5Bytes(getPassword()+2);
        byte[] md5Bytes2 = MD5Utils.getMd5Bytes(getPassword()+3);
        System.arraycopy(md5Bytes1,0,bytes32,0,16);
        System.arraycopy(md5Bytes2,0,bytes32,16,16);
        return bytes32;
    }

    /**
     *
     * @return 16 byte value
     */
    default byte[] getMd5() {
        byte[] md5Bytes1 = MD5Utils.getMd5Bytes(getPassword());
        return md5Bytes1;
    }

    /**
     *
     * @return 16 byte value
     */
    default byte[] getMd51() {
        byte[] md5Bytes1 = MD5Utils.getMd5Bytes(getPassword()+1);
        return md5Bytes1;
    }
}
