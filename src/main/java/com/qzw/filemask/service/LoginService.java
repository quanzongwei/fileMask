package com.qzw.filemask.service;

import com.qzw.filemask.component.GlobalPasswordHolder;

import javax.swing.*;

/**
 * @author quanzongwei
 * @date 2020/3/15
 */
public class LoginService {

    /**
     * 登录逻辑
     */
    public static void doLogin(JFrame f) {
        //第一次启用该软件,初始化密码
        if (!AuthenticationService.isExistUserPassword()) {
            String password = passwordInitializationDialog(f);
            AuthenticationService.setUserMd5Byte(password);
            GlobalPasswordHolder.setPassword(password);
            return;
        }
        //登录认证
        authentication(f);
        //如果软件密码初始化完成, 同时当前用户是合法用户, 那么,程序正常运行
        return;
    }

    /**
     * 认证
     */
    private static void authentication(JFrame f) {
        String password = authenticationDialog(f);
        if (password == null || password.equals("")) {
            nullCHeck(f, 1);
        } else {
            if (!AuthenticationService.isCurrentUser(password)) {
                JOptionPane.showConfirmDialog(f, "对不起密码错误,请重新输入", "提示", JOptionPane.DEFAULT_OPTION);
                authentication(f);
            }
        }
        //认证成功,设置全局密码
        GlobalPasswordHolder.setPassword(password);
    }

    /**
     * 密码初始化
     */
    private static String passwordInitializationDialog(JFrame f) {
        String passwordFirst = JOptionPane.showInputDialog(f, "您第一次使用该软件, 请设置密码:");
        if (passwordFirst == null || passwordFirst.equals("")) {
            return nullCHeck(f, 0);
        } else {
            if (!isValidPassword(passwordFirst)) {
                JOptionPane.showConfirmDialog(f, "密码不合法,请重新输入!(只允许包含数字和字母,位数是1-20位)", "提示", JOptionPane.DEFAULT_OPTION);
                return passwordInitializationDialog(f);
            }
            String passwordSecond = JOptionPane.showInputDialog(f, "请再次确认密码(忘记密码会导致文件无法解密)!");
            if (passwordSecond == null || passwordSecond.equals("")) {
                return nullCHeck(f, 0);
            } else {
                if (!passwordFirst.equals(passwordSecond)) {
                    JOptionPane.showConfirmDialog(f, "两次密码不相同, 请重新输入!", "提示", JOptionPane.DEFAULT_OPTION);
                    return passwordInitializationDialog(f);
                }
            }
        }
        return passwordFirst;
    }

    /**
     * 认证对话框
     */
    private static String authenticationDialog(JFrame f) {
        String password = JOptionPane.showInputDialog(f, "请输入密码:");
        if (password == null || password.equals("")) {
            return nullCHeck(f, 1);
        }
        if (!isValidPassword(password)) {
            JOptionPane.showConfirmDialog(f, "密码不合法,请重新输入!(只允许包含数字和字母,位数是1-20位)", "提示", JOptionPane.DEFAULT_OPTION);
            return authenticationDialog(f);
        }
        return password;
    }

    /**
     * 判断密码是否合法
     */
    public static boolean isValidPassword(String password) {
        if (password.matches("[A-Za-z0-9]{1,20}")) {
            return true;
        }
        return false;
    }

    /**
     * 不合法输入检查
     *
     * @param opType 0:密码初始化 1:认证
     * @return 返回用户重新输入的密码
     */
    private static String nullCHeck(JFrame f, int opType) {
        int value = JOptionPane.showConfirmDialog(f, "您未输入任何有效的数据,软件即将退出!(点击取消可以返回重新输入)", "提示", JOptionPane.OK_CANCEL_OPTION);
        if (value == JOptionPane.CANCEL_OPTION) {
            //密码初始化
            if (opType == 0) {
                return passwordInitializationDialog(f);
            }
            //认证
            else if (opType == 1) {
                return authenticationDialog(f);
            }
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        System.exit(0);
        return null;
    }
}
