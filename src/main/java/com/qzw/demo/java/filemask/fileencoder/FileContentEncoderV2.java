package com.qzw.demo.java.filemask.fileencoder;

import com.qzw.demo.java.filehide.ByteUtil;
import com.qzw.demo.java.filemask.absclass.AbstractFileEncoderV2;
import com.qzw.demo.java.filemask.component.PasswordHolder;
import com.qzw.demo.java.filemask.enums.DirChooseEnum;
import com.qzw.demo.java.filemask.enums.FileEncoderTypeEnum;
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
public class FileContentEncoderV2 extends AbstractFileEncoderV2 {
    private static final int SIZE_1024 = 1024;

    @Override
    public FileEncoderTypeEnum getFileEncoderType() {
        return FileEncoderTypeEnum.FILE_CONTENT_ENCODE;
    }


    @Override
    protected byte[][] encryptOriginFile(File fileOrDir, byte[] extraParam) {
        try {
            encodeOrDecodeFile(fileOrDir, extraParam, true);
            byte[][] result = new byte[2][];
            // just return a not null value that indicates encrypt operation completed
            return result;
        } catch (IOException e) {
            log.info("文件使用中,加密失败,{}", fileOrDir.getPath());
            return null;
        }
    }

    @Override
    protected boolean decryptOriginFile(File fileOrDir, byte[] extraParam) {
        try {
            encodeOrDecodeFile(fileOrDir, extraParam, false);
            // decryption success
            return true;
        } catch (IOException e) {
            log.info("文件使用中,解密失败,{}", fileOrDir.getPath());
            return false;
        }
    }


    /**
     * @param encodeMap encodeMap.length=256
     * @throws IOException
     */
    private void encodeOrDecodeFile(File file, byte[] encodeMap, boolean isEncodeOperation) throws IOException {
        byte[] decodeMap = new byte[256];
        for (int i = 0; i < encodeMap.length; i++) {
            decodeMap[ByteUtil.getUnsignedByte(encodeMap[i])] = (byte) i;
        }
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            long length = raf.length();
            long blockNum = length / SIZE_1024;
            Long remain = length % SIZE_1024;
            for (long i = 0; i < blockNum; i++) {
                byte[] b1 = new byte[SIZE_1024];
                raf.seek(0 + i * SIZE_1024);
                raf.read(b1, 0, SIZE_1024);
                for (int j = 0; j < b1.length; j++) {
                    if (isEncodeOperation) {
                        b1[j] = encodeMap[ByteUtil.getUnsignedByte(b1[j])];
                    } else {
                        b1[j] = decodeMap[ByteUtil.getUnsignedByte(b1[j])];
                    }
                }
                raf.seek(0 + i * SIZE_1024);
                raf.write(b1);
            }
            // 尾部数据处理
            if (remain > 0) {
                byte[] b3 = new byte[remain.intValue()];
                raf.seek(0 + blockNum * SIZE_1024);
                raf.read(b3, 0, remain.intValue());
                for (int j = 0; j < b3.length; j++) {
                    if (isEncodeOperation) {
                        b3[j] = encodeMap[ByteUtil.getUnsignedByte(b3[j])];
                    } else {
                        b3[j] = decodeMap[ByteUtil.getUnsignedByte(b3[j])];
                    }
                }
                raf.seek(0 + blockNum * SIZE_1024);
                raf.write(b3);
            }
        } catch (IOException ex) {
            log.info("文件使用中," + (isEncodeOperation ? "加密" : "解密" + "失败,{}"), file.getPath());
            throw ex;
        }
    }

    @Test
    public void testEncode() {
        PasswordHolder.password = "123456";
        encodeFileOrDir(new File("D:\\Data\\测试\\cc"), DirChooseEnum.CASCADE_DIR);
    }

    @Test
    public void testDecode() {
        PasswordHolder.password = "123456";
        decodeFileOrDir(new File("D:\\Data\\测试\\cc"), DirChooseEnum.CASCADE_DIR);
    }
}
