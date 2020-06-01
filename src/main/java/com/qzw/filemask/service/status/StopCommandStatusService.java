package com.qzw.filemask.service.status;

/**
 * 停止运行命令状态服务
 * 场景: 程序接收到用户提前停止运行的命令后,在安全点终止程序运行
 *
 * @author quanzongwei
 * @date 2020/6/1
 */
public class StopCommandStatusService {
    /**
     * 0:无需停止
     * 1:需要在安全点停止
     */
    private static Integer stopStatus = 0;

    /**
     * 无需停止
     */
    public static Integer STOP_STATUS_NOT_REQUIRE_STOP = 0;

    /**
     * 需要在安全点停止
     */
    public static Integer STOP_STATUS_REQUIRE_STOP = 1;

    public synchronized static Integer getStopStatus() {
        return stopStatus;
    }

    public synchronized static void setStopStatus(Integer stopStatus) {
        StopCommandStatusService.stopStatus = stopStatus;
    }
}
