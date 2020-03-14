package com.qzw.demo.java.filemask.util;

import com.qzw.demo.java.filehide.MD5Utils;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * @author BG388892
 * @date 2020/3/7
 */
@Log4j2
public class AuthenticationUtils {

    static String AUTH_FILE_NAME = File.separatorChar + "auth.fileMask";
    static String AUTH_DIR_NAME = File.separatorChar + "authentication";
    public static boolean isExistUserPassword() {
        String dir = System.getProperty("user.dir") + AUTH_DIR_NAME;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        String authFilePath = dir + AUTH_FILE_NAME;
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
        String dir = System.getProperty("user.dir") + AUTH_DIR_NAME;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        String authFilePath = dir + AUTH_FILE_NAME;
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
        String dir = System.getProperty("user.dir") + AUTH_DIR_NAME;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }
        String authFilePath = dir + AUTH_FILE_NAME;
        File authFile = new File(authFilePath);
        try (RandomAccessFile raf = new RandomAccessFile(authFile, "rw")) {
            raf.setLength(0);
            raf.write(MD5Utils.getMd5Bytes(password));
        } catch (Exception ex) {
            log.error("认证文件访问失败, path", authFile, ex);
        }
    }
}
