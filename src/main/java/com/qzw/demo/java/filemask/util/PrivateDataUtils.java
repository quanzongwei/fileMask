package com.qzw.demo.java.filemask.util;

import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author BG388892
 * @date 2020/3/3
 */
@Log4j2
public class PrivateDataUtils {

    public static String PRIVATE_DATA_DIR = ".fileMask";
    public static String PRAVATE_String = "fileMask";


    /**
     * 私有数据文件夹
     *
     * @param targetFileOrDir
     * @return
     */
    public static File getPrivateDataDir(File targetFileOrDir) {
        return new File(targetFileOrDir.getParent() + File.separatorChar + PRIVATE_DATA_DIR);
    }

    /**
     * 私有数据文件
     *
     * @param targetFileOrDir
     * @return
     */
    public static File getPrivateDataFile(File targetFileOrDir) {
        return new File(targetFileOrDir.getParent() + File.separatorChar + PRIVATE_DATA_DIR + File.separatorChar + targetFileOrDir.getName());
    }

    public static boolean isFileMaskFile(File file) {
        boolean fileMask = file.getPath().contains(PRIVATE_DATA_DIR) || file.getPath().contains(PRAVATE_String);
        if (fileMask) {
            return true;
        }
        return false;
    }

    /**
     * 同步方法
     */
    public static synchronized Integer getAutoIncrementSequence4ParentDir(File fileOrDir) {
        String fmvalueFile = getPrivateDataDir(fileOrDir).getPath() + File.separatorChar + ".fmvalue";
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

    @Test
    public void test() {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile("D:\\Data\\测试\\test\\xxx.txt","rw");
            raf.seek(0);
            if (raf.length() == 0) {
                raf.writeInt(0);
                System.out.println(0);
                return;
            }
            int sequence = raf.readInt();
            raf.seek(0);
            raf.writeInt(sequence + 1);
            System.out.println(sequence);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
