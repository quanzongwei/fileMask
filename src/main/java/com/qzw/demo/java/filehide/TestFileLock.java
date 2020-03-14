package com.qzw.demo.java.filehide;

import org.junit.Test;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author BG388892
 * @date 2020/1/6
 */
public class TestFileLock {

    /**
     * 设置只读和可写
     * 输出:
     * 可写
     * 只读
     * 可写
     *
     * @throws IOException
     */
    @Test
    public void test() throws IOException {
        File file = new File("D:\\Data\\admin.txt");
        if (file.canWrite()) {
            System.out.println("可写");
        } else {
            System.out.println("只读");
        }
        file.setReadOnly();

        if (file.canWrite()) {
            System.out.println("可写");
        } else {
            System.out.println("只读");
        }
        file.setWritable(true);
        if (file.canWrite()) {
            System.out.println("可写");
        } else {
            System.out.println("只读");
        }
    }

    /**
     * 文件锁定有问题
     * 释放后,txt中还是所有内容都没了,这个很奇怪不建议使用
     * docx被word打开后, 这里直接报错: 另一个程序正在使用此文件，进程无法访问
     * 所以强烈不建议使用
     */
    @Test
    public void testFileOpen() throws IOException {
        File file = new File("D:\\Data\\admin2.docx");

        //此处直接报错了
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel channel = raf.getChannel();


        FileLock lock = channel.tryLock();
        if (lock.isValid()) {
            System.out.println("文件被锁定");

        } else {
            System.out.println("文件未锁定");
        }
        lock.release();
        if (lock.isValid()) {
            System.out.println("文件被锁定");

        } else {
            System.out.println("文件未锁定");
        }
        System.out.println();

        raf.close();
    }

    /**
     * 文件不存在, 不报错
     * png尾部加字节, 能打开; 但是改字节就打不开了
     *
     * @throws IOException
     */
    @Test
    public void testFileOpenVSimple() throws IOException {
//        File file = new File("D:\\Data\\admin.txt");
        File file = new File("D:\\Data\\admin2.docx");

        //文件被进程占用此处直接报错了,
        //错误:java.io.FileNotFoundException: D:\Data\admin2.docx (另一个程序正在使用此文件，进程无法访问。)
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(raf.length());
        raf.writeByte(0);
        raf.writeByte(32);
        raf.writeByte(65);
        raf.seek(0);
        raf.writeByte(0);
        raf.writeByte(0);
        raf.writeByte(0);
        raf.writeByte(0);
        raf.writeByte(0);
        raf.writeByte(0);
        raf.writeByte(0);
        raf.writeByte(0);
        raf.close();
    }

    /**
     * 使用renameTo, 判断文件锁定
     * txt和mp4和png一样 打开也是未锁定, 但是打开的mp4执行比较耗时
     * 文件不存在也是锁定
     * <p>
     * docx 打开是锁定, 关闭是未锁定
     * <p>
     * 不会报错文件不存在,同时会新建一个文件
     * <p>
     * 执行rename操作即使是不存在一个文件也会新建
     */
    @Test
    public void testFileLock() throws IOException {
//        File file = new File("D:\\Data\\admin2.docx");
//        File file = new File("D:\\Data\\走之儿.mp4");
        File file = new File("D:\\Data\\png - 副本.png");

        boolean success = file.renameTo(file);
        if (success) {
            System.out.println("未锁定");
        } else {
            System.out.println("锁定");
        }
    }


    /**
     * 使用move, 判断文件锁定
     * <p>
     * xlsx打开就报错, 另一个进程正在使用此文件; 未打开不报错
     * docx打开就报错, 另一个进程正在使用此文件; 未打开不报错
     * docx打开就报错, 另一个进程正在使用此文件; 未打开不报错
     * mp4和txt和png一样打开也是未锁定
     * txt notepad打开也是未锁定状态, 文本即使打开也是未锁定
     * <p>
     * word打开文档, 运行程序,文件被占用:FileSystemException: D:\Data\admin3.xlsx -> D:\Data\admin3.xlsx: 另一个程序正在使用此文件，进程无法访问。
     * <p>
     * 路径不存在, 报错:文件不存在
     * <p>
     * 五星推荐, 目前这种方式应该是最合理的
     */
    @Test
    public void testFileMove() throws IOException {
//        File file = new File("D:\\Data\\admin3.xlsx");//
//        File file = new File("D:\\Data\\admin.txt");//
//        File file = new File("D:\\Data\\admin2.docx");
//        File file = new File("D:\\Data\\走之儿.mp4");

        File file = new File("D:\\Data\\png - 副本.png");

        //此处会抛文件系统异常
        Files.move(Paths.get(file.getPath()),
                Paths.get(file.getPath()), StandardCopyOption.ATOMIC_MOVE);

    }

    /**
     * 使用OutputStream, 判断文件是否正在被使用
     * <p>
     * xlsx打开就报错, 另一个进程正在使用此文件; 未打开不报错
     * docx打开就报错, 另一个进程正在使用此文件; 未打开不报错
     * .mp4打开就报错, 另一个进程正在使用此文件; 未打开不报错
     * txt notepad打开也是未锁定状态, 文本即使打开也是未锁定
     * <p>
     * 这个效果和move的效果是一样的
     * <p>
     * word打开文档, 运行程序,文件不存在错误:java.io.FileNotFoundException: D:\Data\admin2.docx (另一个程序正在使用此文件，进程无法访问。)
     * <p>
     * 路径不存在, 会在路径下新建文件
     * <p>
     * 存在bug: mp4执行两次打开FileOutPutStream, 然后文件就损坏了, 所以, 又否定一种方式, png格式的文件也有这个问题
     * <p>
     * 三星推荐
     */
    @Test
    public void testFileIO() throws IOException {
//        File file = new File("D:\\Data\\admin3.xlsx");//
//        File file = new File("D:\\Data\\admin.txt");//
//        File file = new File("D:\\Data\\admin2.docx222444");
//        File file = new File("D:\\Data\\走之儿.mp4");
        File file = new File("D:\\Data\\png - 副本.png");
        FileOutputStream fos = new FileOutputStream(file);
        fos.flush();
        fos.close();

    }

    //这个好像测试不了, 文件夹重命名的时候鼠标移开就命名成功了, 无法debug

    /**
     * 所以假设,可能存在文件夹锁的问题
     * 理论上文件夹是不会正在被使用的, 所以影响的只可能是文件, 所以影响范围其实不大. 但是还是可能会有影响
     *
     * @throws IOException
     */
    @Test
    public void testDirRenameFalse() throws IOException {
        File file = new File("D:\\Data");
        boolean suc = file.renameTo(file);

    }

    /**
     * 重命名方法
     * 1. 目前没抛出过异常
     * 2. 传成功返回true
     * 3. 失败返回false,如果文件正在被word使用, 则返回false, 重命名失败; 如果是文本文件, notepad打开, name会重命名成功
     */
    @Test
    public void testOpenRename() {
        File file = new File("D:\\Data\\a\\admin.txt");
        boolean success = file.renameTo(new File("D:\\Data\\a\\admin2.txt"));

    }

    @Test
    public void testexeFail() throws IOException {
        File file = new File("C:\\Users\\BG388892\\Desktop\\filehideV100.exe");
//        FileOutputStream fileOutputStream = new FileOutputStream(file,true);

        if (file.exists()) {
            System.out.println("文件存在");
        } else {
            System.out.println("文件不存在");
            return;
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        System.out.println(raf.length());
        int read = raf.read();
        System.out.println(read);
        raf.seek(raf.length());
        String str = "AAAAA";
        Integer i = 0;
        raf.write(i.byteValue());
        raf.write(str.getBytes());
        i = 5;
        raf.write(i.byteValue());

        raf.close();


        System.out.println();
        System.out.println(str.getBytes().length);
    }

    @Test
    public void testexe() throws IOException {
        File file = new File("C:\\Users\\BG388892\\Desktop\\filehideV100.exe");
//        FileOutputStream fileOutputStream = new FileOutputStream(file,true);

        if (file.exists()) {
            System.out.println("文件存在");
        } else {
            System.out.println("文件不存在");
            return;
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        System.out.println(raf.length());
        int read = raf.read();
        System.out.println(read);
        raf.seek(78);
        String str = "AAAAA";
        raf.write(str.getBytes());

        raf.close();


        System.out.println();
        System.out.println(str.getBytes().length);
    }

    /**
     * 正确的代码
     * 但是问题是写四个1也可以, 这个就郁闷了; 可能是程序运行中,某一小块不可以吧
     */
    @Test
    public void testexeFromMicrosoft大牛() throws IOException {
//        File file = new File("C:\\Users\\BG388892\\Desktop\\filehideV100.exe");
        File file = new File("D:\\360极速浏览器下载\\JsonView\\JsonView\\JsonView - 副本.exe");
//        FileOutputStream fileOutputStream = new FileOutputStream(file,true);

        if (file.exists()) {
            System.out.println("文件存在");
        } else {
            System.out.println("文件不存在");
            return;
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        System.out.println(raf.length());
        int read = raf.read();
        System.out.println(read);
        raf.seek(raf.length());
        String str = "AAAAA";
        Integer i = 0;
        System.out.println(i.byteValue());
        // 写四个字节
        raf.write(i.byteValue());
        raf.write(i.byteValue());
        raf.write(i.byteValue());
        raf.write(i.byteValue());
        //写内容
        raf.write(str.getBytes());
        //写内容长度
        raf.write(i.byteValue());
        raf.write(i.byteValue());
        raf.write(i.byteValue());

        i = 5;
        raf.write(i.byteValue());

        raf.close();


        System.out.println();
        System.out.println(str.getBytes().length);
    }
}
