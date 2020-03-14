package com.qzw.demo.java.filehide;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author BG388892
 * @date 2020/1/4
 */
public class FileHeadReplace {

    /**
     * 在执行一次就恢复了呀
     * <p>
     * 太棒了 操作成功
     * <p>
     * 经过测试没问题OK
     * <p>
     * 一般情况下
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        RandomAccessFile raf;
//        raf = new RandomAccessFile(new File("E:\\咕泡二期-pandownload-要啥下啥\\idea20192\\田英章亲授硬笔教程\\田英章最新间架结构\\竖折撇.mp4"),"rw");
        raf = new RandomAccessFile(new File("D:\\Data\\admin.txt"), "rw");
        raf = new RandomAccessFile(new File("E:\\咕泡二期-pandownload-要啥下啥\\idea20192\\田英章亲授硬笔教程\\田英章最新间架结构\\草字头.mp4"), "rw");
        raf.seek(0);
        // 100个字节的文件总是有的吧, 不好意思, 可能是没有的哦
        Long byteAmount = raf.length() < 100L ? raf.length() : 100L;
        byte[] bytes = new byte[byteAmount.intValue()];
        raf.length();

        raf.read(bytes, 0, byteAmount.intValue());
        raf.seek(0);

        //这个并不是交换
        for (int i = 0; i < bytes.length; i++) {
            raf.write(bytes[bytes.length - 1 - i]);
        }

        raf.close();


        // 文件删除数据用
//        raf.setLength();
    }

}
