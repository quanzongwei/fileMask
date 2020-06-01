package com.qzw.filemask.fileencoder;

import com.qzw.filemask.enums.ChooseTypeEnum;
import com.qzw.filemask.enums.FileEncoderTypeEnum;
import com.qzw.filemask.enums.MaskExceptionEnum;
import com.qzw.filemask.exception.MaskException;
import com.qzw.filemask.interfaces.FileEncoderType;
import com.qzw.filemask.service.StatisticsService;
import com.qzw.filemask.service.TailService;
import com.qzw.filemask.service.status.StopCommandStatusService;
import com.qzw.filemask.service.PrivateDataService;
import lombok.extern.log4j.Log4j2;

import java.io.File;

/**
 * 加密解密抽象类
 *
 * @author quanzongwei
 * @date 2020/1/18
 */
@Log4j2
public abstract class AbstractFileEncoder implements FileEncoderType {
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
            if (PrivateDataService.isFileMaskFile(fileOrDir)) {
                log.info("私有数据文件无需处理, {}", fileOrDir.getPath());
                return;
            }
            //文件选择方式:单文件
            if (dirChooseEnum.equals(ChooseTypeEnum.FILE_ONLY)) {
                executeEncrypt(fileOrDir);
                if (ifReceiveStopCommand()) {
                    return;
                }
            }
            //文件选择方式:文件夹
            else if (dirChooseEnum.equals(ChooseTypeEnum.CURRENT_DIR_ONLY)) {
                File[] files = fileOrDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (PrivateDataService.isFileMaskFile(file)) {
                            log.info("私有数据文件无需处理, {}", file.getPath());
                            continue;
                        }
                        executeEncrypt(file);
                        if (ifReceiveStopCommand()) {
                            return;
                        }
                    }
                }
                executeEncrypt(fileOrDir);
                if (ifReceiveStopCommand()) {
                    return;
                }
            }
            //文件选择方式:级联文件夹
            else if (dirChooseEnum.equals(ChooseTypeEnum.CASCADE_DIR)) {
                File[] files = fileOrDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        //cascade directory
                        if (file.isDirectory()) {
                            encodeFileOrDir(file, ChooseTypeEnum.CASCADE_DIR);
                            if (ifReceiveStopCommand()) {
                                return;
                            }
                            continue;
                        }
                        executeEncrypt(file);
                        if (ifReceiveStopCommand()) {
                            return;
                        }
                    }
                }
                executeEncrypt(fileOrDir);
                if (ifReceiveStopCommand()) {
                    return;
                }
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
            if (PrivateDataService.isFileMaskFile(fileOrDir)) {
                log.info("私有数据文件无需处理,{}", fileOrDir.getPath());
                return;
            }
            //文件选择方式:单文件
            if (dirChooseEnum.equals(ChooseTypeEnum.FILE_ONLY)) {
                executeDecrypt(fileOrDir);
                if (ifReceiveStopCommand()) {
                    return;
                }
            }
            //文件选择方式:文件夹
            else if (dirChooseEnum.equals(ChooseTypeEnum.CURRENT_DIR_ONLY)) {
                File[] files = fileOrDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (PrivateDataService.isFileMaskFile(file)) {
                            log.info("私有数据文件无需处理, {}", file.getPath());
                            continue;
                        }
                        executeDecrypt(file);
                        if (ifReceiveStopCommand()) {
                            return;
                        }
                    }
                }
                executeDecrypt(fileOrDir);
                if (ifReceiveStopCommand()) {
                    return;
                }
            }
            //文件选择方式:级联文件夹
            else if (dirChooseEnum.equals(ChooseTypeEnum.CASCADE_DIR)) {
                File[] files = fileOrDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        //cascade directory
                        if (file.isDirectory()) {
                            decodeFileOrDir(file, ChooseTypeEnum.CASCADE_DIR);
                            if (ifReceiveStopCommand()) {
                                return;
                            }
                            continue;
                        }
                        executeDecrypt(file);
                        if (ifReceiveStopCommand()) {
                            return;
                        }
                    }
                }
                executeDecrypt(fileOrDir);
                if (ifReceiveStopCommand()) {
                    return;
                }
            }
        }
    }


    public void executeEncrypt(File fileOrDir) {
        FileEncoderTypeEnum fileEncoderType = getFileEncoderType();
        if (fileOrDir.isDirectory() && !fileEncoderType.isSupportEncryptDir()) {
            //加密方式不支持加密文件夹, 直接跳过, 不需要任何日志
            return;
        }
        Long begin = System.currentTimeMillis();
        //[统计] 设置当前文件加密开始时间
        StatisticsService.setCurrentFileOperationBeginTime(System.currentTimeMillis());
        //[统计] 设置当前文件名称
        StatisticsService.setCurrentFileName(fileOrDir.getName());
        //[统计] 设置当前文件所在文件夹
        StatisticsService.setCurrentFileParentName(fileOrDir.getParent());
        //[统计] 设置当期文件总大小
        StatisticsService.setCurrentFileBytes(fileOrDir.isDirectory() ? 0 : fileOrDir.length());

        //核心逻辑: 执行加密 不抛出异常
        TailService.encryptByType(fileOrDir, fileEncoderType);

        long end = System.currentTimeMillis();
        //[统计] 已完成文件总数+1
        StatisticsService.setDoneFileTotalAmount(StatisticsService.getDoneFileTotalAmount() + 1);
        if (StatisticsService.isIfCurrentFileExecuteContentEncrypt()) {
            //do nothing
        } else {
            //[统计] 增加非内容加密文件耗时
            StatisticsService.setDoneFileAmount4NotContentEncrypt(StatisticsService.getDoneFileAmount4NotContentEncrypt() + 1);
            //[统计] 非内容加密文件总数+1
            StatisticsService.setDoneFileAmountSpendTime4NotFileContentEncrypt(StatisticsService.getDoneFileAmountSpendTime4NotFileContentEncrypt() + (end - begin));
        }
        StatisticsService.clearCurrentFileInfo();

    }


    public void executeDecrypt(File fileOrDir) {
        if (PrivateDataService.isFileMaskFile(fileOrDir)) {
            log.info("私有数据文件无需处理,{}", fileOrDir.getPath());
            return;
        }

        Long begin = System.currentTimeMillis();
        //[统计] 设置当前文件加密开始时间
        StatisticsService.setCurrentFileOperationBeginTime(System.currentTimeMillis());
        //[统计] 设置当前文件路径名称
        StatisticsService.setCurrentFileName(fileOrDir.getName());
        //[统计] 设置当前文件所在文件夹
        StatisticsService.setCurrentFileParentName(fileOrDir.getParent());
        //[统计] 设置当期文件总大小
        StatisticsService.setCurrentFileBytes(fileOrDir.isDirectory() ? 0 : fileOrDir.length());

        //核心逻辑: 执行解密 不抛出异常
        TailService.decryptAllType(fileOrDir);

        long end = System.currentTimeMillis();
        //[统计] 已完成文件总数+1
        StatisticsService.setDoneFileTotalAmount(StatisticsService.getDoneFileTotalAmount() + 1);
        if (StatisticsService.isIfCurrentFileExecuteContentEncrypt()) {
            //do nothing
        } else {
            //[统计] 增加非内容加密文件耗时
            StatisticsService.setDoneFileAmount4NotContentEncrypt(StatisticsService.getDoneFileAmount4NotContentEncrypt() + 1);
            //[统计] 非内容加密文件总数+1
            StatisticsService.setDoneFileAmountSpendTime4NotFileContentEncrypt(StatisticsService.getDoneFileAmountSpendTime4NotFileContentEncrypt() + (end - begin));
        }
        //[统计] 清空当前文件统计信息
        StatisticsService.clearCurrentFileInfo();
    }

    /**
     * 是否收到提前停止命令
     */
    private boolean ifReceiveStopCommand() {
        if (StopCommandStatusService.getStopStatus().equals(StopCommandStatusService.STOP_STATUS_REQUIRE_STOP)) {
            return true;
        }
        return false;
    }
}
