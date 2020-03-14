package com.qzw.filemask.enums;

import lombok.Getter;

/**
 * 加密类型
 * @author quanzongwei
 * @date 2020/1/18
 */
public enum FileEncoderTypeEnum {
    /**
     * 文件名称加密
     */
    FILE_OR_DIR_NAME_ENCODE(1, true),
    /**
     * 文件头部加密
     */
    FILE_HEADER_ENCODE(2, false),
    /**
     * 文件内容加密
     */
    FILE_CONTENT_ENCODE(3, false);


    /**
     * 加密类型
     */
    @Getter
    private int type;

    /**
     * 该加密类型是否支持对文件夹本身加密, 例如对文件夹名称进行加密
     */
    @Getter
    private boolean supportEncryptDir;

    FileEncoderTypeEnum(int type, boolean supportEncryptDir) {
        this.type = type;
        this.supportEncryptDir = supportEncryptDir;
    }
}
