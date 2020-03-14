package com.qzw.demo.java.filemask.enums;

/**
 * @author BG388892
 * @date 2020/1/18
 */
public enum DirChooseEnum {
    /**
     * 当前文件或者文件夹
     */
    FILE_ONLY(10),
    /**
     * 加密当前文件夹下的所有文件(包括本身)
     */
    CURRENT_DIR_ONLY(20),
    /**
     * 级联加密当前文件夹下的所有文件(包括本身)
     */
    CASCADE_DIR(30);

    public int getOperationType() {
        return operationType;
    }

    private int operationType;

    DirChooseEnum(int type) {
        this.operationType = type;
    }
}
