package com.qzw.filemask.util;

import com.qzw.filemask.Constants;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * 登录认证
 * @author quanzongwei
 * @date 2020/3/7
 */
@Log4j2
public class AuthenticationUtils {


    public static boolean isExistUserPassword() {
        String dir = getAuthDirName();
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        String authFilePath = dir + Constants.AUTH_FILE_NAME;
        File authFile = new File(authFilePath);
        if (!authFile.exists()) {
            return false;
        }
        try (RandomAccessFile raf = new RandomAccessFile(authFile, "rw")) {
            if (raf.length() == 0) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            log.error("认证文件访问失败, path", authFile, ex);
        }
        return false;
    }

    public static boolean isCurrentUser(String password) {
        String dir = getAuthDirName();
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        String authFilePath = dir + Constants.AUTH_FILE_NAME;
        File authFile = new File(authFilePath);
        if (!authFile.exists()) {
            return false;
        }
        try (RandomAccessFile raf = new RandomAccessFile(authFile, "rw")) {
            if (raf.length() == 0) {
                return false;
            }
            byte[] inputMd5Bytes = MD5Utils.getMd5Bytes(password);
            // md5 数据的长度为16字节
            byte[] userMd5Bytes = new byte[inputMd5Bytes.length];
            raf.seek(0);
            raf.read(userMd5Bytes);
            if (Arrays.equals(userMd5Bytes, inputMd5Bytes)) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            log.error("认证文件访问失败, path", authFile, ex);
        }
        return false;
    }

    public static void setUserMd5Byte(String password) {
        String dir = getAuthDirName();
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        String authFilePath = dir + Constants.AUTH_FILE_NAME;
        File authFile = new File(authFilePath);
        try (RandomAccessFile raf = new RandomAccessFile(authFile, "rw")) {
            raf.setLength(0);
            raf.write(MD5Utils.getMd5Bytes(password));
        } catch (Exception ex) {
            log.error("认证文件访问失败, path", authFile, ex);
        }
    }

    private static String getAuthDirName() {
        return System.getProperty("user.dir") + Constants.AUTH_DIR_NAME;
    }
}
