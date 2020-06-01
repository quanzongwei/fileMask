package com.qzw.filemask.service.status;

/**
 * 操作锁状态服务
 * 场景: 所有的加密和解密必须获取到该锁才能执行
 *
 * @author BG388892
 * @date 2020/6/1
 */
public class OperationLockStatusService {
    /**
     * 0:未开始解密或者解密
     * 1:开始加密或者解密
     */
    private static Integer runStatus = 0;
    /**
     * 未开始解密或者解密
     */
    public static Integer RUN_STATUS_NOT_START = 0;
    /**
     * 开始加密或者解密
     */
    public static Integer RUN_STATUS_STARTED = 1;

    public synchronized static boolean lock() {
        if (runStatus.equals(RUN_STATUS_STARTED)) {
            return false;
        }
        runStatus = RUN_STATUS_STARTED;
        return true;
    }

    public synchronized static void releaseLock() {
        runStatus = RUN_STATUS_NOT_START;

    }
}
