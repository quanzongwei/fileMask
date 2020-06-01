package com.qzw.filemask.service;

import com.qzw.filemask.util.DisplayInHumanUtils;

import java.math.BigDecimal;

/**
 * 数据统计服务
 * 场景:用于统计文件处理进度
 *
 * @author BG388892
 * @date 2020/5/22
 */

public class StatisticsService {
    /**
     * 扫描文件总数:
     * 1. 加密:文件名称加密会包含文件夹
     * 2. 解密:包括文件和文件夹
     */
    public static Long scanFileTotalAmount = 0L;
    /**
     * 已处理完成文件总数,包含不需要处理的文件
     */
    public static Long doneFileTotalAmount = 0L;
    /**
     * 待处理总字节数
     * 具体包括:
     * 1. 加密: 未使用文件内容加密的; 当前用户且仅使用文件名称加密
     * 2. 解密: 该文件被当前用户使用文件内容加密
     */
    public static Long todoFileTotalBytes = 0L;
    /**
     * 已完成处理字节总数
     */
    public static Long doneFileTotalBytes = 0L;
    /**
     * 已完成 非内容加密的文件总数
     */
    public static Long doneFileAmount4NotContentEncrypt = 0L;
    /**
     * 加密开始时间
     */
    public static Long operationBeginTime = 0L;
    /**
     * 已处理字节耗时
     */
    public static Long doneFileTotalBytesSpendTime = 0L;
    /**
     * 样本文件花费时间(即排除使用全文加密的文件)
     */
    public static Long doneFileAmountSpendTime4NotFileContentEncrypt = 0L;


    /**
     * 当前文件名称
     */
    public static String currentFileName = "";
    /**
     * 当前文件所在目录
     */
    public static String currentFileParentName = "";
    /**
     * 当前文件是否需要执行全文加密
     */
    public static boolean ifCurrentFileExecuteContentEncrypt = false;
    /**
     * 当前文件总字节数
     */
    public static Long currentFileBytes = 0L;
    /**
     * 处理当前文件开始时间
     */
    public static Long currentFileOperationBeginTime = 0L;
    /**
     * 当前文件已处理完成的字节数(仅针对全文加密有效)
     */
    public static Long currentFileCompletedBytes = 0L;


    public static String getCurrentFileParentName() {
        return currentFileParentName;
    }

    public static void setCurrentFileParentName(String currentFileParentName) {
        StatisticsService.currentFileParentName = currentFileParentName;
    }

    public static Long getScanFileTotalAmount() {
        return scanFileTotalAmount;
    }

    public static void setScanFileTotalAmount(Long scanFileTotalAmount) {
        StatisticsService.scanFileTotalAmount = scanFileTotalAmount;
    }

    public static Long getDoneFileTotalAmount() {
        return doneFileTotalAmount;
    }

    public static void setDoneFileTotalAmount(Long doneFileTotalAmount) {
        StatisticsService.doneFileTotalAmount = doneFileTotalAmount;
    }

    public static Long getTodoFileTotalBytes() {
        return todoFileTotalBytes;
    }

    public static void setTodoFileTotalBytes(Long todoFileTotalBytes) {
        StatisticsService.todoFileTotalBytes = todoFileTotalBytes;
    }

    public static Long getDoneFileTotalBytes() {
        return doneFileTotalBytes;
    }

    public static void setDoneFileTotalBytes(Long doneFileTotalBytes) {
        StatisticsService.doneFileTotalBytes = doneFileTotalBytes;
    }

    public static Long getDoneFileAmount4NotContentEncrypt() {
        return doneFileAmount4NotContentEncrypt;
    }

    public static void setDoneFileAmount4NotContentEncrypt(Long doneFileAmount4NotContentEncrypt) {
        StatisticsService.doneFileAmount4NotContentEncrypt = doneFileAmount4NotContentEncrypt;
    }

    public static Long getOperationBeginTime() {
        return operationBeginTime;
    }

    public static void setOperationBeginTime(Long operationBeginTime) {
        StatisticsService.operationBeginTime = operationBeginTime;
    }

    public static Long getDoneFileTotalBytesSpendTime() {
        return doneFileTotalBytesSpendTime;
    }

    public static void setDoneFileTotalBytesSpendTime(Long doneFileTotalBytesSpendTime) {
        StatisticsService.doneFileTotalBytesSpendTime = doneFileTotalBytesSpendTime;
    }

    public static Long getDoneFileAmountSpendTime4NotFileContentEncrypt() {
        return doneFileAmountSpendTime4NotFileContentEncrypt;
    }

    public static void setDoneFileAmountSpendTime4NotFileContentEncrypt(Long doneFileAmountSpendTime4NotFileContentEncrypt) {
        StatisticsService.doneFileAmountSpendTime4NotFileContentEncrypt = doneFileAmountSpendTime4NotFileContentEncrypt;
    }

    public static String getCurrentFileName() {
        return currentFileName;
    }

    public static void setCurrentFileName(String currentFileName) {
        StatisticsService.currentFileName = currentFileName;
    }

    public static Long getCurrentFileBytes() {
        return currentFileBytes;
    }


    public static void setCurrentFileBytes(Long currentFileBytes) {
        StatisticsService.currentFileBytes = currentFileBytes;
    }

    public static Long getCurrentFileOperationBeginTime() {
        return currentFileOperationBeginTime;
    }

    public static void setCurrentFileOperationBeginTime(Long currentFileOperationBeginTime) {
        StatisticsService.currentFileOperationBeginTime = currentFileOperationBeginTime;
    }

    public static Long getCurrentFileCompletedBytes() {
        return currentFileCompletedBytes;
    }

    public static void setCurrentFileCompletedBytes(Long currentFileCompletedBytes) {
        StatisticsService.currentFileCompletedBytes = currentFileCompletedBytes;
    }

    public static boolean isIfCurrentFileExecuteContentEncrypt() {
        return ifCurrentFileExecuteContentEncrypt;
    }

    public static void setIfCurrentFileExecuteContentEncrypt(boolean ifCurrentFileExecuteContentEncrypt) {
        StatisticsService.ifCurrentFileExecuteContentEncrypt = ifCurrentFileExecuteContentEncrypt;
    }


    /**
     * 已经花费时间(精确到秒)
     */
    public static String generateTotalSpendTimeInHuman() {
        if (getOperationBeginTime() == 0L) {
            return 0 + "秒";
        }
        long second = (System.currentTimeMillis() - getOperationBeginTime()) / 1000;
        return DisplayInHumanUtils.getSecondInHuman(second);
    }

    /**
     * 已经花费时间(精确到毫秒)
     */
    public static String generateTotalSpendTimeMillisecondsInHuman() {
        if (getOperationBeginTime() == 0L) {
            return 0 + "秒";
        }
        long millisecond = (System.currentTimeMillis() - getOperationBeginTime());
        return DisplayInHumanUtils.getMilliSecondInHuman(millisecond);
    }

    /**
     * 剩余时间(精确到秒)
     */
    public static String generateLastTime() {
        long total = 0L;
        long total4File = 0L;
        long total4Bytes = 0L;

        Long time4Bytes = getDoneFileTotalBytesSpendTime();
        Long time4File = getDoneFileAmountSpendTime4NotFileContentEncrypt();

        Long todoFileTotalBytes = getTodoFileTotalBytes();
        Long doneFileTotalBytes = getDoneFileTotalBytes();

        Long scanFileTotalAmount = getScanFileTotalAmount();
        Long doneFileAmount4NotContentEncrypt = getDoneFileAmount4NotContentEncrypt();
        Long doneFileAmount = getDoneFileTotalAmount();

        if (!todoFileTotalBytes.equals(0L) && !doneFileTotalBytes.equals(0L)) {
            BigDecimal b4Bytes = BigDecimal.valueOf(todoFileTotalBytes - doneFileTotalBytes).divide(BigDecimal.valueOf(doneFileTotalBytes), 6, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(time4Bytes));
            total4Bytes = b4Bytes.longValue();
        }

        if (!scanFileTotalAmount.equals(0L) && !doneFileAmount4NotContentEncrypt.equals(0L)) {
            BigDecimal b4File = BigDecimal.valueOf(scanFileTotalAmount - doneFileAmount).divide(BigDecimal.valueOf(doneFileAmount4NotContentEncrypt), 6, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(time4File));
            total4File = b4File.longValue();
        }

        total = total4Bytes + total4File;

        return DisplayInHumanUtils.getSecondInHuman(total / 1000);
    }

    /**
     * 当前文件剩余执行时间(精确到秒)
     */
    public static String computeCurrentFileLastTimeInHuman() {
        long encryptFileBytes = getCurrentFileCompletedBytes();
        long spentTime = getCurrentFileEncryptSpentTime();
        if (encryptFileBytes == 0L || spentTime == 0L || currentFileBytes == 0L) {
            //特殊情况
            return 0 + "秒";
        }
        BigDecimal value = BigDecimal.valueOf(currentFileBytes - encryptFileBytes).divide(BigDecimal.valueOf(encryptFileBytes), 7, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(spentTime));
        return DisplayInHumanUtils.getSecondInHuman(value.intValue());
    }

    /**
     * 获取当前文件加密已耗时长
     */
    public static long getCurrentFileEncryptSpentTime() {
        if (currentFileOperationBeginTime == 0L) {
            return 0;
        }
        return (System.currentTimeMillis() - currentFileOperationBeginTime) / 1000;
    }


    /**
     * 获取待处理总字节数
     */
    public static String getTodoFileTotalBytesInHuman() {
        long b = todoFileTotalBytes;
        return DisplayInHumanUtils.getBytesInHuman(b);
    }


    /**
     * 获取当前文件总大小
     */
    public static String getCurrentFileBytesInhuman() {
        return DisplayInHumanUtils.getBytesInHuman(currentFileBytes);
    }

    /**
     * 获取当前文件已处理字节总大小
     */
    public static String getCurrentFileEncryptBytesInHuman() {
        return DisplayInHumanUtils.getBytesInHuman(currentFileCompletedBytes);
    }

    /**
     * 获取当前文件加密已耗时长
     */
    public static String getCurrentFileEncryptSpentTimeInHuman() {
        if (currentFileOperationBeginTime == 0L) {
            return 0 + "秒";
        }
        return DisplayInHumanUtils.getSecondInHuman((System.currentTimeMillis() - currentFileOperationBeginTime) / 1000);
    }

    public static String getCurrentFileParentPath() {
        return currentFileParentName;
    }

    /**
     * 所有数据初始化
     * 场景:每次加密或者解密完成后
     */
    public static void clearAll() {
        todoFileTotalBytes = 0L;
        scanFileTotalAmount = 0L;
        doneFileAmount4NotContentEncrypt = 0L;
        doneFileTotalAmount = 0L;

        operationBeginTime = 0L;
        doneFileTotalBytes = 0L;

        doneFileTotalBytesSpendTime = 0L;
        doneFileAmountSpendTime4NotFileContentEncrypt = 0L;

        currentFileName = "";
        currentFileParentName = "";
        ifCurrentFileExecuteContentEncrypt = false;
        currentFileBytes = 0L;
        currentFileOperationBeginTime = 0L;
        currentFileCompletedBytes = 0L;
    }

    /**
     * 清空当前文件统计信息
     */
    public static void clearCurrentFileInfo() {
        StatisticsService.setCurrentFileBytes(0L);
        StatisticsService.setCurrentFileName("");
        currentFileParentName = "";
        ifCurrentFileExecuteContentEncrypt = false;
        currentFileCompletedBytes = 0L;
    }
}
