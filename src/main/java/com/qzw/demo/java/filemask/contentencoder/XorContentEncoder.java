package com.qzw.demo.java.filemask.contentencoder;

import com.qzw.demo.java.filemask.absclass.AbstractContentEncoder;

/**
 * @author BG388892
 * @date 2020/1/18
 */
public class XorContentEncoder extends AbstractContentEncoder {


    private byte[] secretKey;
    /**
     * @param secretKey 32字节长度
     */
    public void setSecretKey(byte[] secretKey) {

    }

    @Override
    protected byte[] encodeContent(byte[] content) {
        return new byte[0];
    }

    @Override
    protected byte[] decodeContent(byte[] Encryptedcontent) {
        return new byte[0];
    }
}
