package com.qzw.filemask.fileencoder;

import com.qzw.filemask.enums.FileEncoderTypeEnum;
import lombok.extern.log4j.Log4j2;

/**
 * 加密类型三: 文件内容加密
 * 原理:xor加密理论上无法破解,唯一的缺陷是无法抵御已知明文攻击;
 * 改加密类型使用xor+uuid的方式,防止已知明文攻击获取用户秘钥,
 * 具有极快的加密速度和绝对的安全性
 *
 *
 * 该方式支持军事机密级别的文件加密,无法通过任何手段进行解密
 * 请保存好您的密码
 * @author quanzongwei
 * @date 2020/1/18
 */
@Log4j2
public class FileContentEncoder extends AbstractFileEncoder {

    @Override
    public FileEncoderTypeEnum getFileEncoderType() {
        return FileEncoderTypeEnum.FILE_CONTENT_ENCODE;
    }
}
