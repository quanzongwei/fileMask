package com.qzw.filemask.fileencoder;

import com.qzw.filemask.enums.FileEncoderTypeEnum;
import lombok.extern.log4j.Log4j2;

/**
 * 加密类型一: 文件名称加密
 * 原理: 文件重命名;同时每个文件夹会在私有数据文件夹.fileMask中的.fmvalue中保存递增的序号,该序号就是加密后的文件名
 *
 * 文件和文件夹的处理方式不同
 * 文件: 名称加密后,追加到文件末尾
 * 文件夹: 保存在私有数据文件夹.fileMask中,这样做的原因是文件夹无法追加数据
 *
 * @author quanzongwei
 * @date 2020/1/18
 */
@Log4j2
public class FileOrDirNameEncoder extends AbstractFileEncoder {
    @Override
    public FileEncoderTypeEnum getFileEncoderType() {
        return FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE;
    }
}
