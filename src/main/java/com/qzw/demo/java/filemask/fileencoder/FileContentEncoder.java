package com.qzw.demo.java.filemask.fileencoder;

import com.qzw.demo.java.filehide.ByteUtil;
import com.qzw.demo.java.filemask.absclass.AbstractFileEncoder;
import com.qzw.demo.java.filemask.component.PasswordHolder;
import com.qzw.demo.java.filemask.enums.DirChooseEnum;
import com.qzw.demo.java.filemask.enums.FileEncoderTypeEnum;
import com.qzw.demo.java.filemask.exception.MaskException;
import com.qzw.demo.java.filemask.interfaces.FileEncoderType;
import com.qzw.demo.java.filemask.interfaces.PrivateDataAccessor;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author BG388892
 * @date 2020/1/18
 */
@Log4j2
public class FileContentEncoder extends AbstractFileEncoder implements PrivateDataAccessor, FileEncoderType {
    private static final int SIZE_1024 = 1024;

    @Override
    public FileEncoderTypeEnum getFileEncoderType() {
        return FileEncoderTypeEnum.FILE_CONTENT_ENCODE;
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
                contentEncode(fileOrDir);
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
                        contentEncode(file);
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
                        contentEncode(file);
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
                contentDecode(fileOrDir);
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
                        contentDecode(file);
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
                        decodeFile(file, dirChooseEnum);
                        continue;
                    }
                    if (decodePermit(file, getFileEncoderType())) {
                        contentDecode(file);
                    }
                }
            }
        }
    }

    private void contentEncode(File file) {
        byte[] encodeMap = generateEncodeMap();
        encodeOrDecodeFile(file, encodeMap, true);
    }

    private void contentDecode(File fileOrDir) {
        byte[] encodeMap = retrievePrivateData(fileOrDir, getFileEncoderType());
        encodeOrDecodeFile(fileOrDir, encodeMap, false);
    }


    /**
     * @param encodeMap encodeMap.length=256
     * @throws IOException
     */
    private void encodeOrDecodeFile(File file, byte[] encodeMap, boolean isEncodeOperation) {
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

            if (isEncodeOperation) {
                logPrivateData(encodeMap, file, getFileEncoderType());
            } else {
                removeEncodeFlag(file, getFileEncoderType());
            }
        } catch (IOException ex) {
            log.info("文件使用中," + (isEncodeOperation ? "加密" : "解密" + "失败,{}"), file.getPath());
        }
    }

    private byte[] generateEncodeMap() {
        byte[] encodeMap = new byte[256];
        Random rd = new Random();
        //todo 更加随机 +秘钥
        rd.setSeed(System.currentTimeMillis());
        byte[] rdBytes = new byte[256];
        rd.nextBytes(rdBytes);
        List<Byte> bList = new ArrayList<>();
        for (Integer i = 0; i < 256; i++) {
            bList.add(i.byteValue());
        }
        List<Byte> mappedList = new ArrayList<>();
        for (int i = 0; i < rdBytes.length; i++) {
            byte b = rdBytes[i];
            if (bList.contains(b)) {
                mappedList.add(b);
                bList.remove(bList.indexOf(b));
                bList = bList.stream().collect(Collectors.toList());
            } else {
                // conflict resolve
                int index = rd.nextInt(bList.size());
                mappedList.add(bList.get(index));
                bList.remove(index);
                bList = bList.stream().collect(Collectors.toList());
            }
        }
        for (int i = 0; i < mappedList.size(); i++) {
            encodeMap[i] = mappedList.get(i);
        }
        return encodeMap;
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
