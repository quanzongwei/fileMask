package com.qzw.filemask.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author BG388892
 * @date 2020/6/1
 */
public class ThreadPoolUtil {

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
