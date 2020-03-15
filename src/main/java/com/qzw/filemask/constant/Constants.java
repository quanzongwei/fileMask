package com.qzw.filemask.constant;

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


    /**
     * 私有数据文件夹
     */
    public static String PRIVATE_DATA_DIR = ".fileMask";

    /**
     * 项目名称
     */
    public static String PRAVATE_String = "fileMask";

    /**
     * 保存文件夹自增数据的文件名称
     */
    public static String FILE_NAME_4_AUTO_INCREMENT_SEQUENCE = ".fmvalue";

    /**
     * doc path
     */
    public static String DOC_PATH = System.getProperty("user.dir") + File.separatorChar + "doc" + File.separatorChar + "readme.html";

}
