package com.qzw.filemask.fileencoder;

import com.qzw.filemask.Constants;
import com.qzw.filemask.enums.FileEncoderTypeEnum;
import com.qzw.filemask.util.PrivateDataUtils;
import lombok.extern.log4j.Log4j2;

import java.io.File;

/**
 * 加密类型一: 文件名称加密
 * 原理: 文件名称重命名,把原始文件名称使用encodeMap加密后,保存在私有数据文件中
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
            log.info("加密类型一,序列号获取失败,加密失败,{}", fileOrDir.getPath());
            return null;
        }
        String originName = fileOrDir.getName();
        String targetName = (fileOrDir.isDirectory() ? Constants.DIR_MASK_PREFIX : Constants.FILE_MASK_PREFIX) + sequence;
        String targetPath = fileOrDir.getParent() + File.separatorChar + targetName;
        boolean b = fileOrDir.renameTo(new File(targetPath));
        if (!b) {
            log.info("加密类型一,加密失败,{}", fileOrDir.getPath());
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
