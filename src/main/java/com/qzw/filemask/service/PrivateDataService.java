package com.qzw.filemask.service;

import com.qzw.filemask.constant.Constants;
import com.qzw.filemask.util.RandomStrUtils;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * fileMask私有数据
 * 作用: 用于保存加密后的秘钥, 或者被加密信息的原文
 *
 * @author quanzongwei
 * @date 2020/3/3
 */
@Log4j2
public class PrivateDataService {
    /**
     * 获取目标文件对应的私有数据文件夹
     */
    public static File getPrivateDataDir(File targetFileOrDir) {
        File file = new File(targetFileOrDir.getParent() + File.separatorChar + Constants.PRIVATE_DATA_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
        String sets = "attrib +H \"" + file.getAbsolutePath() + "\"";
        try {
            Runtime.getRuntime().exec(sets);
        } catch (IOException e) {
            log.info("文件隐藏失败:filePath" + file.getAbsolutePath());
        }
        return file;
    }

    /**
     * 获取目标文件对应的私有数据文件
     */
    public static File getPrivateDataFile(File targetFileOrDir) {
        return new File(targetFileOrDir.getParent() + File.separatorChar + Constants.PRIVATE_DATA_DIR + File.separatorChar + targetFileOrDir.getName());
    }

    /**
     * 获取目标文件对应的私有数据文件
     * 用于:release v2 版本
     * Object String or Integer
     */
    public static File getPrivateDataFileReleaseV2(File targetFileOrDir, Object sequence) {
        return new File(targetFileOrDir.getParent() + File.separatorChar + Constants.PRIVATE_DATA_DIR + File.separatorChar + sequence);
    }

    /**
     * 判断文件是否是私有数据文件
     */
    public static boolean isFileMaskFile(File file) {
        boolean fileMask = file.getPath().contains(Constants.PRIVATE_DATA_DIR)
                || file.getPath().contains(Constants.PRAVATE_String)
                || file.getPath().contains(Constants.FILE_MASK_AUTHENTICATION_NAME);
        if (fileMask) {
            return true;
        }
        return false;
    }

    /**
     * 同步方法,获取目标文件所在文件夹对应的自增Id
     * 使用场景：父类加密名称 FM{sequence}XXXXX
     */
    public static synchronized Integer getAutoIncrementSequence4ParentDir(File fileOrDir) {
        String fmvalueFile = getPrivateDataDir(fileOrDir).getPath() + File.separatorChar + Constants.FILE_NAME_4_AUTO_INCREMENT_SEQUENCE;
        File file = new File(fmvalueFile);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.seek(0);
            if (raf.length() == 0) {
                raf.writeInt(1);
                return 0;
            }
            int sequence = raf.readInt();
            raf.seek(0);
            raf.writeInt(sequence + 1);
            return sequence;
        } catch (Exception e) {
            log.info("获取文件下唯一自增值出错,path:{},exception:{}", fileOrDir.getPath(), e.getMessage());
        }
        return null;
    }

    /**
     * 获取加密文件夹名称前缀
     *
     * @return
     */
    public static synchronized String getEncryptedDirNameFromSequenceAndBase64RandomStr(File fileOrDir) {
        Integer sequence = getAutoIncrementSequence4ParentDir(fileOrDir);
        if (sequence == null) {
            // unreachable
            sequence = 0;
        }
        // 文件夹名称加密（随机数长度和数量的关系 5=10亿，4=1千万）
        return Constants.FILE_MASK_PREFIX_NAME_FOR_NAME_ENCRYPT + sequence + RandomStrUtils.generateBase64RandomString(Constants.DIRECTORY_SUFFIX_LENGTH);
    }

    /**
     * 获取加密文件名称前缀
     */
    public static synchronized String getEncryptedFileNameFromUuid() {
        return Constants.FILE_MASK_PREFIX_NAME_FOR_NAME_ENCRYPT + RandomStrUtils.generateUUIDBase64String();
    }
}
