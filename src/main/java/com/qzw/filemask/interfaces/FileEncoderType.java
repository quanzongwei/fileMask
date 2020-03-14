package com.qzw.filemask.interfaces;

import com.qzw.filemask.enums.FileEncoderTypeEnum;

/**
 * 解密类型
 * @author quanzongwei
 * @date 2020/1/18
 */
public interface FileEncoderType {
    /**
     * 获取加密解密类型
     *
     * @see FileEncoderTypeEnum
     */
    FileEncoderTypeEnum getFileEncoderType();
}
