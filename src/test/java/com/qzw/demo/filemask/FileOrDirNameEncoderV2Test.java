package com.qzw.demo.filemask;

import com.qzw.demo.java.filemask.component.PasswordHolder;
import com.qzw.demo.java.filemask.enums.DirChooseEnum;
import com.qzw.demo.java.filemask.fileencoder.FileContentEncoderV2;
import com.qzw.demo.java.filemask.fileencoder.FileHeaderEncoderV2;
import com.qzw.demo.java.filemask.fileencoder.FileOrDirNameEncoderV2;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * @author BG388892
 * @date 2020/1/18
 */
public class FileOrDirNameEncoderV2Test extends BaseTest {

    FileOrDirNameEncoderV2 encoder = new FileOrDirNameEncoderV2();
    FileHeaderEncoderV2 encoder2 = new FileHeaderEncoderV2();
    FileContentEncoderV2 encoder3 = new FileContentEncoderV2();

    // 必须抛出exception吗, 这个 todo test
    // 依赖: 加密文件总数
    @Test
    public void testEncodeType1() throws Exception {
        removeFileAndDir(getBaseFile(), true);
        PasswordHolder.password = "123456";
        int fileamount = createDirAndFile(getBaseFile().getPath(), 3);
        //
        encoder.encodeFileOrDir(firstDirFile(), DirChooseEnum.CASCADE_DIR);
        PasswordHolder.password = "123456";

        validateFileNameFalse(firstDirFileEncryptedByType1(fileamount));
        encoder.decodeFileOrDir(firstDirFileEncryptedByType1(fileamount), DirChooseEnum.CASCADE_DIR);
        validateFileNameTrue(firstDirFile());
    }

    //todo 文件内容少于32个字节咋办
    //todo test 32个字节以下
    @Test
    public void testEncodeType2() throws Exception {
//        removeFileAndDir(getBaseFile(), true);
        PasswordHolder.password = "123456";
        int fileamount = createDirAndFile(getBaseFile().getPath(), 2);
        //
        encoder2.encodeFileOrDir(firstFileFile(), DirChooseEnum.FILE_ONLY);
        PasswordHolder.password = "123456";
        encoder2.decodeFileOrDir(firstFileFile(), DirChooseEnum.FILE_ONLY);
    }

    @Test
    public void testEncodeType2File() throws Exception {
        removeFileAndDir(getBaseFile(), true);
        PasswordHolder.password = "123456";
        int fileamount = createDirAndFile(getBaseFile().getPath(), 2);
        //
        encoder2.encodeFileOrDir(firstFileFile(), DirChooseEnum.FILE_ONLY);
        PasswordHolder.password = "123456";
        encoder2.decodeFileOrDir(firstFileFile(), DirChooseEnum.FILE_ONLY);
    }

    @Test
    public void testEncodeType2File4Dir() throws Exception {
        removeFileAndDir(getBaseFile(), true);
        PasswordHolder.password = "123456";
        int fileamount = createDirAndFile(getBaseFile().getPath(), 2);
        //
        encoder2.encodeFileOrDir(firstDirFile(), DirChooseEnum.FILE_ONLY);
        PasswordHolder.password = "123456";
        encoder2.decodeFileOrDir(firstDirFile(), DirChooseEnum.FILE_ONLY);
    }

    /**
     * raf打开文件夹的时候拒绝访问的,怪不得使用加密方式2和3加密文件夹居然能通过, 这是因为报错了,返回null, 当成加密失败处理了.
     */
    @Test
    public void testRafOpenDir() throws Exception {
        try {
            RandomAccessFile raf = new RandomAccessFile(new File("D:\\Data\\测试\\test"), "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    //todo 文件内容少于32个字节咋办
    @Test
    public void testEncodeType3() throws Exception {
        removeFileAndDir(getBaseFile(), true);
        PasswordHolder.password = "123456";
        int fileamount = createDirAndFile(getBaseFile().getPath(), 3);
        //
        encoder3.encodeFileOrDir(firstDirFile(), DirChooseEnum.CURRENT_DIR_ONLY);
//        encoder2.encodeFileOrDir(new File("D:\\Data\\测试\\test\\dir\\file.txt"),DirChooseEnum.FILE_ONLY);
        PasswordHolder.password = "123456";
        encoder3.decodeFileOrDir(firstDirFile(), DirChooseEnum.CASCADE_DIR);
    }
}
