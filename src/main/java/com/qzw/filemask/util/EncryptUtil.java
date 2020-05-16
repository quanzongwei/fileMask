package com.qzw.filemask.util;

/**
 * @author BG388892
 * @date 2020/5/16
 */
public class EncryptUtil {
    /**
     * 加密内容
     * 使用场景:
     * 1.加密头部数据
     * 2.加密文件内容
     * 3.加密文件名称
     *
     * @param uuid       32位
     * @param md532      32位 由password引申出的值
     * @param originText 需要加密的数据
     * @return 加密后的和原来数据等字节的数据
     */
    public static byte[] encryptContent(byte[] uuid, byte[] md532, byte[] originText) {
        return doXorForContent(uuid, md532, originText);
    }

    /**
     * 解密内容
     * 使用场景:
     * 1.加密头部数据
     * 2.加密文件内容
     * 3.加密文件名称
     *
     * @param uuid          32位
     * @param md532         32位 由password引申出的值
     * @param encryptedText 需要加密的数据
     * @return 解密后的字节数据
     */
    public static byte[] decryptContent(byte[] uuid, byte[] md532, byte[] encryptedText) {
        return doXorForContent(uuid, md532, encryptedText);
    }

    /**
     * 对文件内容进行xor操作
     */
    private static byte[] doXorForContent(byte[] uuid, byte[] md532, byte[] text) {
        byte[] contentEncryptXorKey = new byte[uuid.length];
        for (int i = 0; i < uuid.length; i++) {
            contentEncryptXorKey[i] = (byte) (uuid[i] ^ md532[i]);
        }
        byte[] result = new byte[text.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (text[i] ^ contentEncryptXorKey[i % (contentEncryptXorKey.length)]);
        }
        return result;
    }

    /**
     * 加密uuid数据
     * 使用场景:
     * 1.加密uuid
     *
     * @param md545 32位 由password引申出的值
     * @param uuid  32位
     * @return 加密后的和原来数据等字节的数据
     */
    private static byte[] encryptUuid(byte[] md545, byte[] uuid) {
        return doXorForUuid(md545, uuid);
    }

    /**
     * 解密uuid数据
     * 使用场景:
     * 1.解密uuid
     *
     * @param md545 32位 由password引申出的值
     * @param uuid  32位
     * @return 加密后的和原来数据等字节的数据
     */
    private static byte[] decryptUuid(byte[] md545, byte[] uuid) {
        return doXorForUuid(md545, uuid);
    }

    /**
     * 对uuid字符串数据进行xor操作
     */
    private static byte[] doXorForUuid(byte[] md545, byte[] uuid) {
        byte[] result = new byte[uuid.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (md545[i] ^ uuid[i]);
        }
        return result;
    }
}
