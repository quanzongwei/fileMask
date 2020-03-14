package com.qzw.demo.java.filemask.exception;

import com.qzw.demo.java.filemask.enums.MaskEnum;

/**
 * @author BG388892
 * @date 2020/1/18
 */
public class MaskException extends RuntimeException {
    private int type;

    public MaskException(int type,String message) {
        super(message);
        this.type = type;
    }

    public MaskException(MaskEnum maskEnum) {
        super(maskEnum.getMessage());
        this.type = maskEnum.getType();
    }
}
