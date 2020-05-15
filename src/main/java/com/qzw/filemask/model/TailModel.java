package com.qzw.filemask.model;

import lombok.Data;

/**
 * 文件尾部数据结构
 * <p>
 * 数据结构
 * 16字节: 用户md5
 * 16字节: 加密类型
 * 32字节: uuid(使用md5-4执行xor,加密方式1,2,3都需使用)
 * 4字节: 文件头部(加密方式2使用,md5-23+uuid执行xor)
 * n字节: 文件名称(加密方式1使用,md5-23+uuid执行xor)
 * 8字节: 文件原始长度
 * 16字节: FileMaskTailFlag加密标识
 * <p>
 * 注: 如果加密方式为文件头部加密且文件数据长度<4,则使用全文加密
 *
 * @author BG388892
 * @date 2020/5/13
 */
@Data
public class TailModel {
    public static Integer USER_MD5_LENGTH_16 = 16;
    public static Integer ENCODE_TYPE_FLAG_16 = 16;
    public static Integer UUID_32 = 32;
    public static Integer HEAD_4 = 4;

    public static Integer ORIGIN_SIZE_8 = 8;
    public static Integer TAIL_FLAG_16 = 16;

    public byte[] belongUserMd516;
    public byte[] encodeType16;
    public byte[] uuid32;
    public byte[] head4;
    public byte[] fileNameX;
    public byte[] originTextSize8;
    public byte[] tailFlag16;
}
