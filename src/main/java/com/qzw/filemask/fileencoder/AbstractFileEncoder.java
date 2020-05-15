package com.qzw.filemask.fileencoder;

import com.qzw.filemask.FileMaskMain;
import com.qzw.filemask.enums.ChooseTypeEnum;
import com.qzw.filemask.enums.FileEncoderTypeEnum;
import com.qzw.filemask.enums.MaskExceptionEnum;
import com.qzw.filemask.exception.MaskException;
import com.qzw.filemask.interfaces.FileEncoderType;
import com.qzw.filemask.interfaces.PasswordHandler;
import com.qzw.filemask.util.PrivateDataUtils;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.io.File;

/**
 * 加密解密抽象类
 *
 * todo , 监听关闭事件, 完成文件原子操作
 * @author quanzongwei
 * @date 2020/1/18
 */
@Log4j2
public abstract class AbstractFileEncoder implements FileEncoderType {
    private static JTextArea ta = FileMaskMain.ta;


    /**
     * 确保加解密过程串行执行
     */
    public static Object lock = new Object();

    /**
     * 加密入口
     */
    public void encodeFileOrDir(File fileOrDir, ChooseTypeEnum dirChooseEnum) {
        synchronized (lock) {
            if (!fileOrDir.exists()) {
                throw new MaskException(MaskExceptionEnum.FILE_NOT_EXISTS.getType(), "文件或者文件夹不存在,加密失败," + fileOrDir.getPath());
            }
            if (PrivateDataUtils.isFileMaskFile(fileOrDir)) {
                log.info("私有数据文件无需处理, {}", fileOrDir.getPath());
                return;
            }
            //文件选择方式:单文件
            if (dirChooseEnum.equals(ChooseTypeEnum.FILE_ONLY)) {
                executeEncrypt(fileOrDir);
            }
            //文件选择方式:文件夹
            else if (dirChooseEnum.equals(ChooseTypeEnum.CURRENT_DIR_ONLY)) {
                File[] files = fileOrDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        executeEncrypt(file);
                    }
                }
                executeEncrypt(fileOrDir);
            }
            //文件选择方式:级联文件夹
            else if (dirChooseEnum.equals(ChooseTypeEnum.CASCADE_DIR)) {
                File[] files = fileOrDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        //cascade directory
                        if (file.isDirectory()) {
                            encodeFileOrDir(file, ChooseTypeEnum.CASCADE_DIR);
                            continue;
                        }
                        executeEncrypt(file);
                    }
                }
                executeEncrypt(fileOrDir);
            }
        }
    }

    /**
     * 解密入口
     */
    public void decodeFileOrDir(File fileOrDir, ChooseTypeEnum dirChooseEnum) {
        synchronized (lock) {
            if (!fileOrDir.exists()) {
                throw new MaskException(MaskExceptionEnum.FILE_NOT_EXISTS.getType(), "文件或者文件夹不存在,解密失败, " + fileOrDir.getPath());
            }
            if (PrivateDataUtils.isFileMaskFile(fileOrDir)) {
                log.info("私有数据文件无需处理,{}", fileOrDir.getPath());
                return;
            }
            //文件选择方式:单文件
            if (dirChooseEnum.equals(ChooseTypeEnum.FILE_ONLY)) {
                executeDecrypt(fileOrDir);
            }
            //文件选择方式:文件夹
            else if (dirChooseEnum.equals(ChooseTypeEnum.CURRENT_DIR_ONLY)) {
                File[] files = fileOrDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        executeDecrypt(file);
                    }
                }
                executeDecrypt(fileOrDir);
            }
            //文件选择方式:级联文件夹
            else if (dirChooseEnum.equals(ChooseTypeEnum.CASCADE_DIR)) {
                File[] files = fileOrDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        //cascade directory
                        if (file.isDirectory()) {
                            decodeFileOrDir(file, ChooseTypeEnum.CASCADE_DIR);
                            continue;
                        }
                        executeDecrypt(file);
                    }
                }
                executeDecrypt(fileOrDir);
            }
        }
    }

    private void executeEncrypt(File fileOrDir) {
        FileEncoderTypeEnum fileEncoderType = getFileEncoderType();
        if (fileOrDir.isDirectory() && !fileEncoderType.isSupportEncryptDir()) {
            //加密方式不支持加密文件夹, 直接跳过, 不需要任何日志
            return;
        }
        TailUtil.encryptByType(fileOrDir, fileEncoderType);
    }


    public void executeDecrypt(File fileOrDir) {
        if (PrivateDataUtils.isFileMaskFile(fileOrDir)) {
            log.info("私有数据文件无需处理,{}", fileOrDir.getPath());
            return;
        }
        TailUtil.decryptAllType(fileOrDir);
    }
}
