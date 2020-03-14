package com.qzw.demo.java.filemask;

import com.qzw.demo.java.filemask.component.PasswordHolder;
import com.qzw.demo.java.filemask.enums.DirChooseEnum;
import com.qzw.demo.java.filemask.fileencoder.FileContentEncoderV2;
import com.qzw.demo.java.filemask.fileencoder.FileHeaderEncoderV2;
import com.qzw.demo.java.filemask.fileencoder.FileOrDirNameEncoderV2;
import org.junit.Test;

import java.io.File;

/**
 * @author BG388892
 * @date 2020/1/22
 */
public class MainTest {

//------------------------------加密方式 一 ----------------------------------------------------//

    @Test
    public void testEncodeType1() {
        PasswordHolder.password = "123456";
        new FileOrDirNameEncoderV2().
                encodeFileOrDir(new File("D:\\Data\\测试\\aa"), DirChooseEnum.CASCADE_DIR);
    }

    @Test
    public void testDecodeType1() {
        PasswordHolder.password = "123456";
        new FileOrDirNameEncoderV2().
                decodeFileOrDir(new File("D:\\Data\\测试\\nDDir114"), DirChooseEnum.CASCADE_DIR);
    }

//------------------------------加密方式 二----------------------------------------------------

    @Test
    public void testEncodeType2() {
        PasswordHolder.password = "123456";
        new FileHeaderEncoderV2().
                encodeFileOrDir(new File("D:\\Data\\测试\\aa"), DirChooseEnum.CASCADE_DIR);
    }

    @Test
    public void testDecodeType2() {
        PasswordHolder.password = "123456";
        new FileHeaderEncoderV2().
                decodeFileOrDir(new File("D:\\Data\\测试\\nDDir114"), DirChooseEnum.CASCADE_DIR);
    }
//------------------------------加密方式 三----------------------------------------------------

    @Test
    public void testEncodeType3() {
        PasswordHolder.password = "123456";
        new FileContentEncoderV2().
                encodeFileOrDir(new File("D:\\Data\\测试\\aa"), DirChooseEnum.CASCADE_DIR);
    }

    @Test
    public void testDecodeType3() {
        PasswordHolder.password = "123456";
        new FileContentEncoderV2().
                decodeFileOrDir(new File("D:\\Data\\测试\\nDDir114"), DirChooseEnum.CASCADE_DIR);
    }
}
