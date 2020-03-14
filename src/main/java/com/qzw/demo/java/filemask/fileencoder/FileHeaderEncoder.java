package com.qzw.demo.java.filemask.fileencoder;

import com.qzw.demo.java.filemask.absclass.AbstractFileEncoder;
import com.qzw.demo.java.filemask.component.PasswordHolder;
import com.qzw.demo.java.filemask.enums.DirChooseEnum;
import com.qzw.demo.java.filemask.enums.FileEncoderTypeEnum;
import com.qzw.demo.java.filemask.exception.MaskException;
import com.qzw.demo.java.filemask.interfaces.PrivateDataAccessor;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author BG388892
 * @date 2020/1/18
 */
@Log4j2
public class FileHeaderEncoder extends AbstractFileEncoder implements PrivateDataAccessor {
    @Override
    public FileEncoderTypeEnum getFileEncoderType() {
        return FileEncoderTypeEnum.FILE_HEADER_ENCODE;
    }

    @Override
    protected void encodeFile(File fileOrDir, DirChooseEnum dirChooseEnum) {
        // 检验文件和chooseType todo
        if (!fileOrDir.exists()) {
            throw new MaskException(1000, "文件或者文件夹不存在解密失败");
        }
        if (dirChooseEnum.equals(DirChooseEnum.FILE_ONLY)) {
            this.mkPrivateDirIfNotExists(fileOrDir);
            if (encodePermit(fileOrDir)) {
                headEncode(fileOrDir);
            }
        } else if (dirChooseEnum.equals(DirChooseEnum.CURRENT_DIR_ONLY)) {
            File[] files = fileOrDir.listFiles();
            if (files != null && files.length > 0) {
                this.mkPrivateDirIfNotExists(files[0]);
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    if (encodePermit(file)) {
                        headEncode(file);
                    }
                }
            }
        } else if (dirChooseEnum.equals(DirChooseEnum.CASCADE_DIR)) {
            File[] files = fileOrDir.listFiles();
            if (files != null && files.length > 0) {
                this.mkPrivateDirIfNotExists(files[0]);
                for (File file : files) {
                    //cascade directory
                    if (file.isDirectory()) {
                        encodeFile(file, DirChooseEnum.CASCADE_DIR);
                        continue;
                    }
                    if (encodePermit(file)) {
                        headEncode(file);
                    }
                }
            }
        }
    }

    @Override
    protected void decodeFile(File fileOrDir, DirChooseEnum dirChooseEnum) {
        if (!fileOrDir.exists()) {
            throw new MaskException(1000, "文件或者文件夹不存在解密失败");
        }
        if (dirChooseEnum.equals(DirChooseEnum.FILE_ONLY)) {
            this.mkPrivateDirIfNotExists(fileOrDir);
            if (decodePermit(fileOrDir, getFileEncoderType())) {
                headDecode(fileOrDir);
            }
        } else if (dirChooseEnum.equals(DirChooseEnum.CURRENT_DIR_ONLY)) {
            File[] files = fileOrDir.listFiles();
            if (files != null && files.length > 0) {
                this.mkPrivateDirIfNotExists(files[0]);
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }
                    if (decodePermit(file, getFileEncoderType())) {
                        headDecode(file);
                    }
                }
            }
        } else if (dirChooseEnum.equals(DirChooseEnum.CASCADE_DIR)) {
            File[] files = fileOrDir.listFiles();
            if (files != null && files.length > 0) {
                this.mkPrivateDirIfNotExists(files[0]);
                for (File file : files) {
                    //cascade directory
                    if (file.isDirectory()) {
                        decodeFile(file, DirChooseEnum.CASCADE_DIR);
                        continue;
                    }
                    if (decodePermit(file, getFileEncoderType())) {
                        headDecode(file);
                    }
                }
            }
        }
    }

    private void headEncode(File fileOrDir) {
        try (RandomAccessFile raf = new RandomAccessFile(fileOrDir, "rw")) {
            raf.seek(0);
            byte[] originHead = new byte[32];
            raf.read(originHead);
            raf.seek(0);
            raf.writeByte(255);
            raf.writeByte(254);
            raf.writeByte(0);
            raf.writeByte(0);
            logPrivateData(originHead, fileOrDir, getFileEncoderType());
        } catch (IOException e) {
            log.info("文件使用中,加密失败,{}", fileOrDir.getPath());
        }
    }

    private void headDecode(File fileOrDir) {
        byte[] targetHeader = retrievePrivateData(fileOrDir, getFileEncoderType());
        byte[] originHeader = new byte[32];
        //todo 加密解密
        originHeader = targetHeader;
        try (RandomAccessFile raf = new RandomAccessFile(fileOrDir, "rw")) {
            raf.seek(0);
            raf.write(originHeader);
            removeEncodeFlag(fileOrDir, getFileEncoderType());
        } catch (IOException e) {
            log.info("文件使用中,解密失败,{}", fileOrDir.getPath());
        }
    }

    @Test
    public void testEncode() {
        PasswordHolder.password = "123456";
        encodeFile(new File("D:\\Data\\测试\\bb"), DirChooseEnum.CASCADE_DIR);
    }

    @Test
    public void testDecode() {
        PasswordHolder.password = "123456";
        decodeFile(new File("D:\\Data\\测试\\bb"), DirChooseEnum.CASCADE_DIR);
    }
}
