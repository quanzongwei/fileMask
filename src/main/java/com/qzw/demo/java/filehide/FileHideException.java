package com.qzw.demo.java.filehide;

/**
 * @author BG388892
 * @date 2020/1/6
 */
public class FileHideException extends RuntimeException {
    public static int FILE_RENAME_ERROR = 1000;
    public static int TARGET_DIR_NOT_EXIST = 1001;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private Integer code;

    public FileHideException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
