package com.qzw.filemask.util;

/**
 * 展示优化
 *
 * @author quanzongwei
 * @date 2020/6/1
 */
public class DisplayInHumanUtils {
    /**
     * 优化时间展示(精确到秒)
     */
    public static String getSecondInHuman(long second) {
        long sec = second;
        long secPart = sec % 60;
        long minutes = sec / 60;
        long minutesPart = minutes % 60;
        long hour = minutes / 60;
        long hourPart = hour % 60;
        long day = hour / 24;
        long dayPart = day;
        String s = "";
        if (dayPart > 0L) {
            s += dayPart + "天";
        }
        if (hourPart > 0L) {
            s += hourPart + "小时";
        }
        if (minutesPart > 0L) {
            s += minutesPart + "分钟";
        }
        if (secPart > 0L) {
            s += secPart + "秒";
        }
        if (s.equals("")) {
            s = "0字节";
        }
        return s;
    }

    /**
     * 优化时间展示(精确到毫秒)
     */
    public static String getMilliSecondInHuman(long millisecond) {
        long millisec = millisecond;
        long milliPart = millisec % 1000;
        long sec = millisec / 1000;
        long secPart = sec % 60;
        long minutes = sec / 60;
        long minutesPart = minutes % 60;
        long hour = minutes / 60;
        long hourPart = hour % 60;
        long day = hour / 24;
        long dayPart = day;
        String s = "";
        if (dayPart > 0L) {
            s += dayPart + "天";
        }
        if (hourPart > 0L) {
            s += hourPart + "小时";
        }
        if (minutesPart > 0L) {
            s += minutesPart + "分钟";
        }
        if (secPart > 0L) {
            s += secPart + "秒";
        }
        if (secPart > 0L) {
            s += milliPart + "毫秒";
        }
        return s;
    }

    /**
     * 获取文件大小(精确到字节)
     */
    public static String getBytesInHuman(long b) {
        long bPart = b % 1024;
        long k = b / 1024;
        long kPart = k % 1024;
        long m = k / 1024;
        long mPart = m % 1024;
        long g = m / 1024;
        long gPart = g;

        String s = "";
        if (gPart > 0L) {
            s += gPart + "G";
        }
        if (mPart > 0L) {
            s += mPart + ",M";
        }
        if (kPart > 0L) {
            s += kPart + ",K";
        }
        if (bPart > 0L) {
            s += bPart + "字节";
        }
        if (s.equals("")) {
            s = "0字节";
        }
        return s;
    }
}
