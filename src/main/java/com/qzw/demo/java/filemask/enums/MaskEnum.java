package com.qzw.demo.java.filemask.enums;

import lombok.Data;

/**
 * @author BG388892
 * @date 2020/1/18
 */
public enum MaskEnum {
    FILE_NOT_EXISTS(101, "文件不存在"),

    USER_INVALID(102,"用户不合法");

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

    private int type;
    private String message;

    MaskEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }
}
