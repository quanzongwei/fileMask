package com.qzw.filemask.enums;

/**
 * @author quanzongwei
 * @date 2020/1/18
 */
public enum MaskExceptionEnum {
    /**
     * 文件不存在
     */
    FILE_NOT_EXISTS(10000, "文件不存在");

    /**
     * 异常类型
     */
    private int type;

    /**
     * 异常内容
     */
    private String message;

    MaskExceptionEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
