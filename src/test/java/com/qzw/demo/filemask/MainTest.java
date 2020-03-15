package com.qzw.demo.filemask;

import com.qzw.filemask.component.GlobalPasswordHolder;
import com.qzw.filemask.enums.ChooseTypeEnum;
import com.qzw.filemask.fileencoder.FileContentEncoder;
import com.qzw.filemask.fileencoder.FileHeaderEncoder;
import com.qzw.filemask.fileencoder.FileOrDirNameEncoder;
import org.junit.Test;

import java.io.File;

/**
 * @author quanzongwei
 * @date 2020/1/22
 */
public class MainTest {

    @Test
    public void testNull() {
        String xx = null;
        test(xx.concat(""));
    }

    public void test(String a) {
        System.out.println(a);
    }

//------------------------------加密方式 一 ----------------------------------------------------//

    @Test
    public void testEncodeType1() {
        GlobalPasswordHolder.password = "123456";
        new FileOrDirNameEncoder().
                encodeFileOrDir(new File("D:\\Data\\测试\\aa"), ChooseTypeEnum.CASCADE_DIR);
    }

    @Test
    public void testDecodeType1() {
        GlobalPasswordHolder.password = "123456";
        new FileOrDirNameEncoder().
                decodeFileOrDir(new File("D:\\Data\\测试\\nDDir114"), ChooseTypeEnum.CASCADE_DIR);
    }

//------------------------------加密方式 二----------------------------------------------------

    @Test
    public void testEncodeType2() {
        GlobalPasswordHolder.password = "123456";
        new FileHeaderEncoder().
                encodeFileOrDir(new File("D:\\Data\\测试\\aa"), ChooseTypeEnum.CASCADE_DIR);
    }

    @Test
    public void testDecodeType2() {
        GlobalPasswordHolder.password = "123456";
        new FileHeaderEncoder().
                decodeFileOrDir(new File("D:\\Data\\测试\\nDDir114"), ChooseTypeEnum.CASCADE_DIR);
    }
//------------------------------加密方式 三----------------------------------------------------

    @Test
    public void testEncodeType3() {
        GlobalPasswordHolder.password = "123456";
        new FileContentEncoder().
                encodeFileOrDir(new File("D:\\Data\\测试\\aa"), ChooseTypeEnum.CASCADE_DIR);
    }

    @Test
    public void testDecodeType3() {
        GlobalPasswordHolder.password = "123456";
        new FileContentEncoder().
                decodeFileOrDir(new File("D:\\Data\\测试\\nDDir114"), ChooseTypeEnum.CASCADE_DIR);
    }
}
