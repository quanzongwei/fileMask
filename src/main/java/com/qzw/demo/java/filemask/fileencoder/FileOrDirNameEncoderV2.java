package com.qzw.demo.java.filemask.fileencoder;

import com.qzw.demo.java.filemask.absclass.AbstractFileEncoderV2;
import com.qzw.demo.java.filemask.component.PasswordHolder;
import com.qzw.demo.java.filemask.enums.DirChooseEnum;
import com.qzw.demo.java.filemask.enums.FileEncoderTypeEnum;
import com.qzw.demo.java.filemask.util.PrivateDataUtils;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.File;

/**
 *
 * todo 新增的文件重名问题 +2就好了
 * todo sequece保存问题
 * @author BG388892
 * @date 2020/1/18
 */
@Log4j2
public class FileOrDirNameEncoderV2 extends AbstractFileEncoderV2 {
    @Override
    public FileEncoderTypeEnum getFileEncoderType() {
        return FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE;
    }

    @Override
    protected byte[][] encryptOriginFile(File fileOrDir, byte[] extraParam) {
        Integer sequence = PrivateDataUtils.getAutoIncrementSequence4ParentDir(fileOrDir);
        if (sequence == null) {
            log.info("加密方式一, 序列号获取失败, 加密失败");
            return null;
        }
        String originName = fileOrDir.getName();
        String targetName = (fileOrDir.isDirectory() ? "nDDir" : "nDFiLe") + sequence;
        String targetPath = fileOrDir.getParent() + File.separatorChar + targetName;
        // todo test change 名字还是原来的名字, 确实是的
        boolean b = fileOrDir.renameTo(new File(targetPath));
        if (!b) {
            log.info("文件加密(方式一)失败,{}", fileOrDir.getPath());
            return null;
        }
        byte[][] result = new byte[2][];
        result[0] = originName.getBytes();
        result[1] = targetName.getBytes();
        return result;
    }

    @Override
    protected boolean decryptOriginFile(File fileOrDir, byte[] extraParam) {
        String originPath = fileOrDir.getParent() + File.separatorChar + new String(extraParam);
        boolean b = fileOrDir.renameTo(new File(originPath));
        if (!b) {
            log.info("文件解密(方式一)失败,{}", fileOrDir.getPath());
            return false;
        }
        return true;
    }

    @Test
    public void testEncode() {
        PasswordHolder.password = "123456";
        encodeFileOrDir(new File("D:\\Data\\测试\\aa"), DirChooseEnum.CASCADE_DIR);
        FileOrDirNameEncoderV2 encoder = new FileOrDirNameEncoderV2();
    }

    @Test
    public void testDecode() {
        PasswordHolder.password = "123456";
        decodeFileOrDir(new File("D:\\Data\\测试\\nDDir114"), DirChooseEnum.CASCADE_DIR);
    }
}
