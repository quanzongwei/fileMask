package com.qzw.demo.java.filemask;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author BG388892
 * @date 2020/1/19
 */
public class Test22 {


    // raf 可以直接seek到一个不存在的位置, 中间都是0x00字节
    @Test
    public void test() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(new File("D:\\Data\\测试\\aa.txt"), "rw")) {
            raf.seek(0);
            raf.seek(32);
            raf.write(0x03);
        }
    }
}
