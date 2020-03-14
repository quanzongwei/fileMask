package com.qzw.filemask.exception;

import com.qzw.filemask.enums.MaskEnum;

/**
 * @author quanzongwei
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
