package com.qzw.filemask.enums;

/**
 *
 * 选择方式
 * @author quanzongwei
 * @date 2020/1/18
 */
public enum ChooseTypeEnum {
    /**
     * 单个文件
     */
    FILE_ONLY(),
    /**
     * 文件夹(包括文件夹自身和文件夹下文件)
     */
    CURRENT_DIR_ONLY(),
    /**
     * 级联文件夹(包括文件夹自身和文件夹下级联的所有文件)
     */
    CASCADE_DIR();

    ChooseTypeEnum() {
    }
}
