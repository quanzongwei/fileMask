package com.qzw.filemask.fileencoder;

import com.qzw.filemask.enums.FileEncoderTypeEnum;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 加密类型二: 文件头部加密
 * 原理: 保存文件头部4字节数据, 然后替换头部4字节,前4个字节为: FF FE 00 00
 * 告诉应用程序,这是个文本文件,而且只用UTF-32, little-endian编码,
 * 所有非文本文件以及非UTF-32, little-endian编码的文本文件都无法
 * 正常打开,由于值修改了文件头部4个字节,所以加密速度极快.
 *
 * 该方式非常巧妙,如果您对编码较为熟悉
 *
 * 如果您需要使用更专业的加密方式(即使专业人士也无法破解), 请使用加密
 * 类型三(文件内容加密,即全文加密)
 * @author quanzongwei
 * @date 2020/1/18
 */
@Log4j2
public class FileHeaderEncoder extends AbstractFileEncoder {

    @Override
    public FileEncoderTypeEnum getFileEncoderType() {
        return FileEncoderTypeEnum.FILE_HEADER_ENCODE;
    }

}
