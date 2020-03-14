package com.qzw.filemask.fileencoder;

import com.qzw.filemask.enums.FileEncoderTypeEnum;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 加密类型二: 文件头部加密
 * @author quanzongwei
 * @date 2020/1/18
 */
@Log4j2
public class FileHeaderEncoder extends AbstractFileEncoder {
    @Override
    public FileEncoderTypeEnum getFileEncoderType() {
        return FileEncoderTypeEnum.FILE_HEADER_ENCODE;
    }

    /**
     * 加密方式2, 加密的文件首部字节数
     */
    private final int HEAD_BYTE_LEN_32 = 32;
    @Override
    protected byte[][] encryptOriginFile(File fileOrDir, byte[] extraParam) {
        try (RandomAccessFile raf = new RandomAccessFile(fileOrDir, "rw")) {
            // 重点测试 todo test
            if (raf.length()< HEAD_BYTE_LEN_32) {
                // 返回null 表示加密不成功
                log.info("文件长度小于32字节, 不支持方式2加密",fileOrDir.getPath());
                return null;
            }
            raf.seek(0);
            byte[] originHead = new byte[32];
            raf.read(originHead);
            raf.seek(0);
            raf.writeByte(255);
            raf.writeByte(254);
            raf.writeByte(0);
            raf.writeByte(0);
            byte[][] result = new byte[2][];
            result[0] = originHead;
            // not used
            result[1] = null;
            return result;
        } catch (IOException e) {
            log.info("文件使用中,加密失败,{}", fileOrDir.getPath());
            return null;
        }
    }

    @Override
    protected boolean decryptOriginFile(File fileOrDir, byte[] extraParam) {
        byte[] originHeader = extraParam;
        try (RandomAccessFile raf = new RandomAccessFile(fileOrDir, "rw")) {
            if (raf.length()< HEAD_BYTE_LEN_32) {
                return false;
            }
            raf.seek(0);
            raf.write(originHeader);
        } catch (IOException e) {
            log.info("文件使用中,解密失败,{}", fileOrDir.getPath());
            return false;
        }
        return true;
    }
}
