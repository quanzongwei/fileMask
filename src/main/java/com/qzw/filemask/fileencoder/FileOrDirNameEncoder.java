package com.qzw.filemask.fileencoder;

import com.qzw.filemask.enums.FileEncoderTypeEnum;
import com.qzw.filemask.util.PrivateDataUtils;
import lombok.extern.log4j.Log4j2;

import java.io.File;

/**
 *
 * todo 新增的文件重名问题 +2就好了
 * todo sequece保存问题
 * @author quanzongwei
 * @date 2020/1/18
 */
@Log4j2
public class FileOrDirNameEncoder extends AbstractFileEncoder {
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
}
