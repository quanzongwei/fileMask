package com.qzw.demo.java.filemask.absclass;

/**
 * @author BG388892
 * @date 2020/1/18
 */
public abstract class AbstractContentEncoder {
    protected abstract byte[] encodeContent(byte[] content);
    protected abstract byte[] decodeContent(byte[] Encryptedcontent);
}
