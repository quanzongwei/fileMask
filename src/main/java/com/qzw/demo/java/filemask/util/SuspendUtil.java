package com.qzw.demo.java.filemask.util;

/**
 * @author BG388892
 * @date 2020/3/8
 */
public class SuspendUtil {
    private static boolean isEncryptingOneFile = false;

    /**
     * 某个文件正在加密中
     */
    public static void setIsEncrypting() {
        isEncryptingOneFile = true;
    }

    /**
     * 某个文件加密完成
     */
    public static void setFinishedEncrypting() {
        isEncryptingOneFile = true;
    }

    /**
     * @return 系统是否正在加密某个文件 true: 是, false:否
     */
    public static boolean isEncryptingOneFile() {
        return isEncryptingOneFile;
    }
}
