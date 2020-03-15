package com.qzw.filemask.component;

/**
 * 用户密码
 * @author quanzongwei
 * @date 2020/1/18
 */
public class GlobalPasswordHolder {
    private static String password;

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        GlobalPasswordHolder.password = password;
    }
}
