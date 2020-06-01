package com.qzw.filemask.service.status;

/**
 * 计算状态服务
 * 场景: 用于提示框文本展示,用于标识程序在哪个阶段运行
 * 0. 计算状态: 计算待加密或解密文件总数
 * 1. 加密/解密状态: 正式开始加密和解密
 *
 * @author quanzongwei
 * @date 2020/6/1
 */
public class ComputingStatusService {
    /**
     * 0:正在计算
     * 1:正在执行加密/解密
     */
    private static Integer computeStatus = 0;

    /**
     * 正在计算
     */
    public static Integer COMPUTE_STATUS_RUNNING_COMPUTING = 0;
    /**
     * 正在执行加密/解密
     */
    public static Integer COMPUTE_STATUS_RUNNING_ENCRYPT = 1;


    public static synchronized Integer getComputeStatus() {
        return computeStatus;
    }

    public static synchronized void setComputeStatus(Integer computeStatus) {
        ComputingStatusService.computeStatus = computeStatus;
    }
}
