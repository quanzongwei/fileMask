package com.qzw.demo.java.filehide;

import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.*;

/**
 * v2 各种编码测试, 加解密算法:头部逆变换n字节, 尾部加上加密后的标志以及头部变换的字节数, 占用1字节, 所以头部变化不能超过255字节
 * v3 删除测试代码, 修改加密方式
 *
 * @author BG388892
 * @date 2020/1/4
 */
@Log4j2
public class FileHeadReplaceV3 {

    static Integer magicInt = 100;// 不允许大于255
    static String magicEncryptWord = "FiLeMaSkEndFiLeMaSkEndFiLeMaSkEndFiLeMaSkEnd";// 校验确认,可能会错误解密呀; 万一出现这个问题联系作者吧

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

    public static void encryptFileContent(String dirOrFilePath) {
        if (dirOrFilePath == null) {
            log.info("文件或者文件夹不存在,加密失败,path:{}", dirOrFilePath);
            return;
        }
        File file = new File(dirOrFilePath);
        if (!file.exists()) {
            log.info("文件或者文件夹不存在,加密失败,path:{}", dirOrFilePath);
            return;
        }

        if (!file.isDirectory()) {
            try {
                encryptOneFile(file);
            } catch (IOException e) {
                log.info("文件加密失败,{}", file.getPath());
                return;
            }
            log.info("文件加密成功,{}", file.getPath());
            return;
        }

        File[] files = file.listFiles();
        if (file == null || files.length <= 0) {
            return;
        }
        for (File one : files) {
            if (one.isDirectory()) {
                encryptFileContent(one.getPath());
                continue;
            }
            //处理文件
            try {
                encryptOneFile(one);
            } catch (IOException e) {
                log.info("文件加密失败,{}", one.getPath());
                return;
            }
            log.info("文件加密成功,{}", one.getPath());
        }
    }

    public static void decryptFileContent(String dirOrFilePath) {
        if (dirOrFilePath == null) {
            log.info("文件或者文件夹不存在解密失败,path:{}", dirOrFilePath);
        }
        File file = new File(dirOrFilePath);
        if (!file.exists()) {
            log.info("文件或者文件夹不存在解密失败,path:{}", dirOrFilePath);
        }

        if (!file.isDirectory()) {
            try {
                decryptOneFile(file);
            } catch (IOException e) {
                log.info("文件解密失败,{}", file.getPath());
                return;
            }
            log.info("文件解密成功,{}", file.getPath());
            return;
        }

        File[] files = file.listFiles();
        if (file == null || files.length <= 0) {
            return;
        }
        for (File one : files) {
            if (one.isDirectory()) {
                decryptFileContent(one.getPath());
                continue;
            }
            //处理文件
            try {
                decryptOneFile(one);
            } catch (IOException e) {
                log.info("文件解密失败,{}", one.getPath());
                return;
            }
            log.info("文件解密成功,{}", one.getPath());
        }
        if (file.isDirectory()) {
            encryptFileContent(dirOrFilePath);
        }
    }

    /**
     * 文件未找到, 就是被其他程序占用了
     */
    public static void encryptOneFile(File file) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            //需要加密的文件的内容字节数必须>=2
            if (raf.length() <= 1) {
                log.info("文件长度过小,无需加密,{}", file.getPath());
                return;
            }
            //
            raf.seek(0);
            //头部加密的字节数
            Long headEncryptByteAmount = raf.length() < magicInt ? raf.length() : magicInt;
            Integer headEncryptByteAmountInt = headEncryptByteAmount.intValue();
            swap(raf, headEncryptByteAmount);
            // 一共存储45字节 44+1
            byte[] encryptWordBytes = magicEncryptWord.getBytes();
            raf.seek(raf.length());
            // 头部加密的字节数(必须小于255)
            raf.write(headEncryptByteAmountInt.byteValue());
            // 加密标志字节数组
            raf.write(encryptWordBytes);
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    private static void swap(RandomAccessFile raf, Long byteAmount) throws IOException {
        byte[] bytes = new byte[byteAmount.intValue()];
        raf.seek(0);
        raf.read(bytes, 0, byteAmount.intValue());
        //这个并不是交换,本质上还是交换
        for (int i = 0; i < bytes.length; i++) {
            raf.write(bytes[bytes.length - 1 - i]);
        }
    }

    /**
     * 文件未找到, 就是被其他程序占用了
     */
    public static void decryptOneFile(File file) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            raf.seek(0);
            //需要加密的文件的内容字节数必须>=2, 头部加密的字节数占用1字节
            if (raf.length() < magicEncryptWord.getBytes().length + 1 + 2) {
                log.info("文件长度过小, 非加密过的文件, 无需解密,{}", file.getPath());
                return;
            }
            byte[] encryptWordBytes = magicEncryptWord.getBytes();
            raf.seek(raf.length() - encryptWordBytes.length - 1);
            byte[] tailBytes = new byte[encryptWordBytes.length + 1];
            raf.read(tailBytes);
            if (isEncryptFile(tailBytes, encryptWordBytes)) {
                raf.seek(0);
                //文件删除
                raf.setLength(raf.length() - encryptWordBytes.length - 1);
                raf.seek(0);
                //文件头部解密
                swap(raf, Byte.toUnsignedLong(tailBytes[0]));
            } else {
                log.info("文件未被加密过, 无需解密,{}", file.getPath());
            }
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    public static boolean isEncryptFile(byte[] tailBytes, byte[] encryptWordBytes) {
        String tailStr = new String(tailBytes, 1, tailBytes.length - 1);
        String encryptWordStr = new String(encryptWordBytes);
        if (tailStr.equals(encryptWordStr)) {
            return true;
        }
        return false;

    }

    @Test
    public void test() {

        byte[] bytes1 = magicEncryptWord.getBytes();

        // 44
        System.out.println(bytes1.length);

    }

    @Test
    public void testEncrypt() {

        FileHeadReplaceV3.encryptFileContent("D:\\Data\\ad.txt");
    }

    @Test
    public void testDecrypt() {
        FileHeadReplaceV3.decryptFileContent("D:\\Data\\ad.txt");
    }


    // png 头部, 结论是不支持文本加密, 非文本文件加密
    @Test
    public void testInvalidATextFile() throws IOException {


//        File file = new File("D:\\Data\\ad.txt");
        File file = new File("D:\\Data\\admin222.docx");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        raf.write(137);
        raf.write(80);
        raf.write(78);
        raf.write(71);
        raf.write(13);
        raf.write(10);
        raf.write(26);
        raf.write(10);
        raf.write(137);
        raf.write(80);
        raf.write(78);
        raf.write(71);
        raf.write(13);
        raf.write(10);
        raf.write(26);
        raf.write(10);
        raf.write(137);
        raf.write(80);
        raf.write(78);
        raf.write(71);
        raf.write(13);
        raf.write(10);
        raf.write(26);
        raf.write(10);

        raf.close();

    }

    // png 头部, 结论是不支持文本加密, 非文本文件加密
    @Test
    public void testJapanCode() throws IOException {


//        File file = new File("D:\\Data\\ad.txt");
        File file = new File("D:\\Data\\acutf.txt");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        raf.write("こんにちは、世界".getBytes("Shift_JIS"));

        raf.close();

    }

    /**
     * 文本文件加密
     * 建议使用utf-32, 他比较通用
     * <p>
     * Bytes Encoding Form
     * 00 00 FE FF UTF-32, big-endian
     * FF FE 00 00 UTF-32, little-endian
     * FE FF UTF-16, big-endian default
     * FF FE UTF-16, little-endian
     * EF BB BF UTF-8
     * <p>
     * 加密算法测试OK
     *
     * 小端编码就很巧妙了哦
     *
     * @throws IOException
     */
    @Test
    public void testUtf32() throws IOException {
        File file = new File("D:\\Data\\测试\\aa");
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        // FE FF 00 00 头部
        // FF FE 测试可以
        raf.writeByte(255);
        raf.writeByte(254);
//        raf.writeByte(0);
//        raf.writeByte(0);
        raf.close();
    }

    /**
     * 输出utf-16的字符, 文件头部会有BOM
     *
     * @throws IOException
     */
    @Test
    public void testUtf16NewFile() throws IOException {
        File file = new File("D:\\Data\\adminUtf16.txt");
        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-16"));
        bf.write(String.valueOf("中国"));
        bf.flush();
        bf.close();

    }
}
