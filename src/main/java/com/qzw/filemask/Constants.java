package com.qzw.filemask;

import java.io.File;

/**
 * @author quanzongwei
 * @date 2020/1/18
 */
public class Constants {

    /**
     * 认证信息文件名称
     */
    public static String AUTH_FILE_NAME = File.separatorChar + "auth.fileMask";
    /**
     * 认证信息所在文件夹
     */
    public static String AUTH_DIR_NAME = File.separatorChar + "authentication";


    /**
     * 文件夹名称加密后的前缀
     */
    public static final String DIR_MASK_PREFIX = "nDDir";
    /**
     * 文件名称加密后的前缀
     */
    public static final String FILE_MASK_PREFIX = "nDFile";

}
