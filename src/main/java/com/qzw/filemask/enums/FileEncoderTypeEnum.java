package com.qzw.filemask.enums;

import lombok.Getter;

/**
 * @author quanzongwei
 * @date 2020/1/18
 */
public enum FileEncoderTypeEnum {
    FILE_OR_DIR_NAME_ENCODE(1, 0, true),
    FILE_HEADER_ENCODE(2, 1, false),
    FILE_CONTENT_ENCODE(3, 2, false);

    public int getType() {
        return type;
    }


    private int type;
    @Getter
    private int flagRelativeIndex;

    @Getter
    private boolean supportEncryptDir;

    FileEncoderTypeEnum(int type, int flagRelativeIndex, boolean supportEncryptDir) {
        this.flagRelativeIndex = flagRelativeIndex;
        this.type = type;
        this.supportEncryptDir = supportEncryptDir;
    }
}
