package com.qzw.demo.java.filemask.contentencoder;

import com.qzw.demo.java.filemask.absclass.AbstractContentEncoder;

/**
 * @author BG388892
 * @date 2020/1/18
 */
public class ByteMappingContentEncoder extends AbstractContentEncoder {

    private byte[] encodeMapping;
    private byte[] decodeMapping;

    /**
     * 长度 256字节
     */
    public void setByteMapping(byte[] encodeMapping) {
        this.encodeMapping = encodeMapping;

        for (int i = 0; i < encodeMapping.length; i++) {
            decodeMapping[encodeMapping[i]] = (byte) i;
        }
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
