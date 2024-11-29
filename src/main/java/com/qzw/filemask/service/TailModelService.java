package com.qzw.filemask.service;

import com.qzw.filemask.constant.Constants;
import com.qzw.filemask.enums.FileEncoderTypeEnum;
import com.qzw.filemask.model.TailModel;
import com.qzw.filemask.util.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件尾部数据结构service
 * <p>
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
 * @author quanzongwei
 * @date 2020/5/13
 */
@Data
@Log4j2
public class TailModelService {
    public static String FILE_MASK_TAIL_FLAG = "FileMaskTailFlag";
    /**
     * 已经加密标志
     */
    public static byte ENCODED_FLAG = (byte) 0x01;

    private static int SIZE_1024 = 1024 * 64;
    /**
     * 文件头部加密阈值,小于阈值的切换为文件内容加密
     */
    private static int FILE_HEAD_ENCODE_THRESHOLD = 4;

    /**
     * 入口方法:按照某种加密类型加密单个文件或者文件夹
     *
     * @param fileOrDir
     * @param fileEncoderType
     */
    public static void encryptByType(File fileOrDir, FileEncoderTypeEnum fileEncoderType) {
        //文件夹加密走单独逻辑
        if (fileOrDir.isDirectory()) {
            if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
                try {
                    encryptOrDecryptDirectoryName(fileOrDir, true);
                } catch (IOException e) {
                    log.info("加密文件夹失败,{}", fileOrDir);
                }
                return;
            }
            return;
        }

        // 流程:尾部数据结构是否存在? 是否存在是当前用户? 是否是使用同一种方式加密? 是否互斥?
        try (RandomAccessFile raf = new RandomAccessFile(fileOrDir, "rw")) {
            boolean existsTail = TailModelService.existsTailModel(raf);
            //不存在
            if (!existsTail) {
                TailModelService.doEncryptFileAndResetTailModel(fileOrDir, raf, fileEncoderType, true);
            }
            //存在
            else {
                TailModel tailModel = getExistsTailModelInfo(raf);
                boolean isCurrentUser = TailModelService.isCurrentUser(tailModel.getBelongUserMd516(), PasswordService.getMd51ForFileAuthentication());
                if (!isCurrentUser) {
                    log.info("文件认证用户失败,{}", fileOrDir);
                    return;
                }
                boolean hasEncryptedByTypeOrConflict = TailModelService.isEncryptedByTypeOrConflict(tailModel, fileEncoderType);
                if (hasEncryptedByTypeOrConflict) {
                    log.info("文件已加密,无需重复加密,{}", fileOrDir);
                    return;
                }
                TailModelService.doEncryptFileAndResetTailModel(fileOrDir, raf, fileEncoderType, false);
            }
        } catch (Exception ex) {
            log.info("文件操作失败,加密操作不成功,{}", fileOrDir.getPath());
            return;
        }

        //IO操作完成后才可以执行重命名操作
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
            String encryptedFileNameFromUuid = PrivateDataService.getEncryptedFileNameFromUuid();
            String targetPath = fileOrDir.getParent() + File.separatorChar + encryptedFileNameFromUuid;
            boolean b = fileOrDir.renameTo(new File(targetPath));
            log.info("文件名称是否加密成功:{},{}", b, fileOrDir.getPath());
        }
    }

    /**
     * 入口方法: 解密文件或者文件夹
     *
     * @param fileOrDir
     */
    public static void decryptAllType(File fileOrDir) {
        // 文件夹走单独逻辑
        if (fileOrDir.isDirectory()) {
            try {
                encryptOrDecryptDirectoryName(fileOrDir, false);
            } catch (IOException e) {
                log.info("解密文件夹失败,{}", fileOrDir);
            }
            return;
        }

        String targetPath = null;
        try (RandomAccessFile raf = new RandomAccessFile(fileOrDir, "rw")) {
            if (!existsTailModel(raf)) {
                log.info("尾部文件不存在,无需解密,{}", fileOrDir);
                return;
            }
            TailModel model = getExistsTailModelInfo(raf);
            boolean currentUser = isCurrentUser(model.getBelongUserMd516(), PasswordService.getMd51ForFileAuthentication());
            if (!currentUser) {
                log.info("文件用户认证失败,解密操作不成功,{}", fileOrDir.getPath());
                return;
            }
            byte[] encodeType16 = model.getEncodeType16();
            if (encodeType16[FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE.getPosition()] == ENCODED_FLAG) {
                targetPath = fileOrDir.getParent() + File.separator + new String(model.getFileNameX(), "UTF-8");
            }
            if (encodeType16[FileEncoderTypeEnum.FILE_HEADER_ENCODE.getPosition()] == ENCODED_FLAG) {
                raf.seek(0);
                raf.write(model.getHead4());

            } else if (encodeType16[FileEncoderTypeEnum.FILE_CONTENT_ENCODE.getPosition()] == ENCODED_FLAG) {
                //执行解密
                doFileContentEncryptionOrDecryption(raf, false, model.getUuid32(), ByteUtil.bytesToLong(model.getOriginTextSize8()));
            }
            //最后直接删除尾部文件,数据恢复正常
            raf.setLength(ByteUtil.bytesToLong(model.getOriginTextSize8()));

        } catch (Exception ex) {
            log.info("文件操作失败,解密操作不成功,{},{}", fileOrDir.getPath(), ex.getMessage());
            return;
        }

        //IO操作完成后才可以执行重命名操作
        if (StringUtils.isNotBlank(targetPath)) {
            boolean b = fileOrDir.renameTo(new File(targetPath));
            log.info("文件名称是否解密成功:{},{}", b, fileOrDir.getPath());
        }
    }

    /**
     * 加密或者解密文件夹
     *
     * @param fileOrDir
     * @param ifEncodeOperation
     * @throws IOException
     */
    private static void encryptOrDecryptDirectoryName(File fileOrDir, boolean ifEncodeOperation) throws IOException {
        // 加密
        if (ifEncodeOperation) {
            String encryptedDirName = PrivateDataService.getEncryptedDirNameFromSequenceAndBase64RandomStr(fileOrDir);
            File existsPrivateDataFile = PrivateDataService.getPrivateDataFileReleaseV2(fileOrDir, fileOrDir.getName());
            if (existsPrivateDataFile.exists()) {
                log.info("文件夹已加密成功,无需重复加密,{}", fileOrDir);
                return;
            }

            File privateDataFile = PrivateDataService.getPrivateDataFileReleaseV2(fileOrDir, encryptedDirName);
            if (!privateDataFile.exists()) {
                try {
                    privateDataFile.createNewFile();
                } catch (IOException e) {
                    log.info("创建私有数据文件失败,{}", fileOrDir);
                    return;
                }
            } else {
                // fixed since v1.2
                log.info("已存在同名私有数据文件,不执行加密{}", fileOrDir);
                return;
            }

            try (RandomAccessFile raf = new RandomAccessFile(privateDataFile, "rw")) {
                TailModelService.doEncryptFileAndResetTailModel(fileOrDir, raf, FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE, true);
            } catch (Exception ex) {
                log.info("私有数据文件保存加密文件夹名称失败,{}", fileOrDir);
                return;
            }
            boolean success = fileOrDir.renameTo(new File(fileOrDir.getParent() + File.separator + encryptedDirName));
            if (!success) {
                log.info("源文件重命名失败,{}", fileOrDir);
                //同时删除私有数据文件
                privateDataFile.delete();
            }
        }
        //解密
        else {
            //目前只有FM开头或数字命名才可能是加密后的文件夹名称（数字是为了兼容历史逻辑）
            if (!(NumberUtils.isCreatable(fileOrDir.getName())
                    || fileOrDir.getName().startsWith(Constants.FILE_MASK_PREFIX_NAME_FOR_NAME_ENCRYPT))) {
                log.info("非加密文件夹,无需解密,{}", fileOrDir);
                return;
            }
            File privateDataFile = PrivateDataService.getPrivateDataFileReleaseV2(fileOrDir, fileOrDir.getName());
            if (!privateDataFile.exists()) {
                log.info("文件夹未加密,无需解密,{}", fileOrDir);
                return;
            }
            TailModel model;
            try (RandomAccessFile raf = new RandomAccessFile(privateDataFile, "rw")) {
                model = getExistsTailModelInfo(raf);
            }
            boolean isCurrentUser = TailModelService.isCurrentUser(model.getBelongUserMd516(), PasswordService.getMd51ForFileAuthentication());
            if (!isCurrentUser) {
                log.info("当前文件夹已被其他用户加密,解密失败,{}", fileOrDir);
                return;
            }
            byte[] fileNameX = model.getFileNameX();
            boolean success = fileOrDir.renameTo(new File(fileOrDir.getParent() + File.separator + new String(fileNameX, "UTF-8")));
            if (success) {
                boolean delete = privateDataFile.delete();
                if (!delete) {
                    log.info("文件夹解密成功,但私有数据文件删除失败,请检查,{}", fileOrDir);
                    return;
                }
                log.info("文件夹解密成功,{}", fileOrDir);
            } else {
                log.info("文件夹解密失败,{}", fileOrDir);
            }
        }
    }

    /**
     * 是否存在尾部数据结构
     * 通过: FileMaskTailFlag标识
     */
    public static boolean existsTailModel(RandomAccessFile raf) throws IOException {
        long length = raf.length();
        if (length < TailModel.MIN_LENGTH) {
            return false;
        }
        raf.seek(length - FILE_MASK_TAIL_FLAG.length());
        byte[] info = new byte[FILE_MASK_TAIL_FLAG.length()];

        raf.read(info);

        String s = new String(info);
        if (s.equals(FILE_MASK_TAIL_FLAG)) {
            return true;
        }
        return false;
    }

    /**
     * 获取原始文本大小
     */
    public static long getOriginTextLength(RandomAccessFile raf) throws IOException {
        raf.seek(raf.length() - TailModel.ORIGIN_SIZE_8 - TailModel.TAIL_FLAG_16);
        byte[] tailSizeByte = new byte[TailModel.ORIGIN_SIZE_8];
        raf.read(tailSizeByte);
        return ByteUtil.bytesToLong(tailSizeByte);
    }

    /**
     * 判断是否已经加密过或者加密类型冲突
     */
    public static boolean isEncryptedByTypeOrConflict(TailModel model, FileEncoderTypeEnum fileEncoderType) throws IOException {
        byte[] encodeTypeFlagByte = model.getEncodeType16();
        byte flag = encodeTypeFlagByte[fileEncoderType.getPosition()];
        if (flag == ENCODED_FLAG) {
            return true;
        }
        //互斥问题处理
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_HEADER_ENCODE)) {
            byte flag2 = encodeTypeFlagByte[FileEncoderTypeEnum.FILE_CONTENT_ENCODE.getPosition()];
            if (flag2 == ENCODED_FLAG) {
                return true;
            }
        }
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
            byte flag2 = encodeTypeFlagByte[FileEncoderTypeEnum.FILE_HEADER_ENCODE.getPosition()];
            if (flag2 == ENCODED_FLAG) {
                return true;
            }
        }

        return false;
    }

    /**
     * 重置尾部数据结构
     * 使用场景:
     * 1. 首次加密文件
     * 2. 非首次加密文件
     * 3. 首次加密文件夹
     *
     * @param firstSetTail 是否是首次设置尾部数据 true:尾部数据结构不存在 false:尾部数据结构已存在
     */
    private static void doEncryptFileAndResetTailModel(File fileOrDir, RandomAccessFile raf, FileEncoderTypeEnum fileEncoderType, boolean firstSetTail) throws IOException {
        TailModel model = new TailModel();
        if (firstSetTail) {
            //如果TestUtil中的uuid不空的话,表示代码正在走单元测试,这个值固定写死; 正常代码逻辑中该值必须为空
            String uuid = TestUtil.uuid;
            if (StringUtils.isBlank(uuid)) {
                uuid = UUID.randomUUID().toString().replaceAll("-", "");
            }
            byte[] md51 = PasswordService.getMd51ForFileAuthentication();
            model.setBelongUserMd516(md51);
            model.setEncodeType16(new byte[TailModel.ENCODE_TYPE_FLAG_16]);
            model.setUuid32(uuid.getBytes());
            model.setHead4(new byte[TailModel.HEAD_4]);
            model.setOriginTextSize8(ByteUtil.longToBytes(raf.length()));
            model.setTailFlag16(TailModelService.FILE_MASK_TAIL_FLAG.getBytes());
        } else {
            model = getExistsTailModelInfo(raf);
        }

        byte[] type16 = model.getEncodeType16();
        if (type16 == null||type16.length==0) {
            type16 = new byte[TailModel.ENCODE_TYPE_FLAG_16];
        }

        //文件名称加密
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
            //设置标记位
            type16[FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE.getPosition()] = ENCODED_FLAG;
            model.setEncodeType16(type16);
            //设置加密文件名
            model.setFileNameX(fileOrDir.getName().getBytes("UTF-8"));
        }

        //文件头部加密
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_HEADER_ENCODE)) {
            if (raf.length() < FILE_HEAD_ENCODE_THRESHOLD) {
                log.info("文件数据不足4字节,切换为全文加密,{}", fileOrDir);
                fileEncoderType = FileEncoderTypeEnum.FILE_CONTENT_ENCODE;
            } else {
                //设置标记位
                type16[FileEncoderTypeEnum.FILE_HEADER_ENCODE.getPosition()] = ENCODED_FLAG;
                model.setEncodeType16(type16);
                raf.seek(0);
                //设置加密头部数据
                byte[] originHead4 = new byte[4];
                raf.read(originHead4);
                model.setHead4(originHead4);
                //执行加密操作
                raf.seek(0);
                raf.writeByte(255);
                raf.writeByte(254);
                raf.writeByte(0);
                raf.writeByte(0);
            }
        }
        //文件全文加密
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
            //[统计] 设置文件是否是全文加密
            StatisticsService.setIfCurrentFileExecuteContentEncrypt(true);

            //设置标记位
            type16[FileEncoderTypeEnum.FILE_CONTENT_ENCODE.getPosition()] = ENCODED_FLAG;
            model.setEncodeType16(type16);
            //全文加密
            doFileContentEncryptionOrDecryption(raf, true, model.getUuid32(), raf.length());
        }

        resetTailModel(raf, model);
    }

    /**
     * 重置尾部数据结构
     *
     * 加密操作同一在此处进行
     */
    private static void resetTailModel(RandomAccessFile raf, TailModel model) throws IOException {
        long length = ByteUtil.bytesToLong(model.getOriginTextSize8());
        //截取
        raf.setLength(length);
        //移动游标
        raf.seek(length);
        raf.write(model.getBelongUserMd516());
        raf.write(model.getEncodeType16());
        raf.write(EncryptUtil.encryptUuid(PasswordService.getMd545ForUuidEncrypt(), model.getUuid32()));
        raf.write(EncryptUtil.encryptContent(model.getUuid32(), PasswordService.getMd523ForContentEncrypt(), model.getHead4()));
        if (model.getFileNameX() != null && model.getFileNameX().length > 0) {
            raf.write(EncryptUtil.encryptContent(model.getUuid32(), PasswordService.getMd523ForContentEncrypt(), model.getFileNameX()));
        }
        // 这个很重要,一定是writeLong才会占用四个字节
        raf.writeLong(length);
        raf.write(model.getTailFlag16());
    }

    /**
     * 使用场景(全文加密):
     * 1. 首次生成尾部数据结构加密
     * 2. 非首次生成尾部数据结构加密
     * 3. 解密
     *
     * @param isEncodeOperation 是否是加密操作
     * @param originTextLength  原始文件长度
     * @throws IOException
     */
    private static void doFileContentEncryptionOrDecryption(RandomAccessFile raf, boolean isEncodeOperation, byte[] originUuidBytes, long originTextLength) throws IOException {
        long length = originTextLength;
        long blockNum = length / SIZE_1024;
        Long remain = length % SIZE_1024;
        for (long i = 0; i < blockNum; i++) {

            long begin = System.currentTimeMillis();

            byte[] bBlock = new byte[SIZE_1024];
            raf.seek(0 + i * SIZE_1024);
            raf.read(bBlock, 0, SIZE_1024);
            if (isEncodeOperation) {
                bBlock = EncryptUtil.encryptContent(originUuidBytes, PasswordService.getMd523ForContentEncrypt(), bBlock);
            } else {
                bBlock = EncryptUtil.decryptContent(originUuidBytes, PasswordService.getMd523ForContentEncrypt(), bBlock);
            }
            raf.seek(0 + i * SIZE_1024);
            raf.write(bBlock);
            long end = System.currentTimeMillis();
            //[统计] 增加所有文件字节数据已加密总时间
            StatisticsService.setDoneFileTotalBytesSpendTime(StatisticsService.getDoneFileTotalBytesSpendTime() + (end - begin));
            //[统计] 增加所有文件已加密大小
            StatisticsService.setDoneFileTotalBytes(StatisticsService.getDoneFileTotalBytes() + bBlock.length);
            //[统计] 增加当前文件已加密大小
            StatisticsService.setCurrentFileCompletedBytes(StatisticsService.getCurrentFileCompletedBytes() + bBlock.length);
        }
        // 尾部数据处理
        if (remain > 0) {

            long begin = System.currentTimeMillis();

            byte[] bRemain = new byte[remain.intValue()];
            raf.seek(0 + blockNum * SIZE_1024);
            raf.read(bRemain, 0, remain.intValue());
            if (isEncodeOperation) {
                bRemain = EncryptUtil.encryptContent(originUuidBytes, PasswordService.getMd523ForContentEncrypt(), bRemain);
            } else {
                bRemain = EncryptUtil.decryptContent(originUuidBytes, PasswordService.getMd523ForContentEncrypt(), bRemain);
            }
            raf.seek(0 + blockNum * SIZE_1024);
            raf.write(bRemain);
            long end = System.currentTimeMillis();
            //[统计] 增加所有文件字节数据已加密总时间
            StatisticsService.setDoneFileTotalBytesSpendTime(StatisticsService.getDoneFileTotalBytesSpendTime() + (end - begin));
            //[统计] 增加所有文件已加密大小
            StatisticsService.setDoneFileTotalBytes(StatisticsService.getDoneFileTotalBytes() + bRemain.length);
            //[统计] 增加当前文件已加密大小
            StatisticsService.setCurrentFileCompletedBytes(StatisticsService.getCurrentFileCompletedBytes() + bRemain.length);
        }
    }

    /**
     * 获取已经存在的TailModel数据结构,同时解密
     * @param raf
     * @return 调用这个方法之前, 需要保证尾部数据结构一定存在的
     * @throws IOException
     */
    public static TailModel getExistsTailModelInfo(RandomAccessFile raf) throws IOException {
        TailModel model = new TailModel();
        long originTextSize = getOriginTextLength(raf);
        raf.seek(originTextSize);
        byte[] tailModelByte = new byte[Math.toIntExact(raf.length() - originTextSize)];
        raf.read(tailModelByte);

        byte[] userMd5 = Arrays.copyOfRange(tailModelByte, 0, TailModel.USER_MD5_LENGTH_16);
        byte[] encodeType16 = Arrays.copyOfRange(tailModelByte, 0 + TailModel.USER_MD5_LENGTH_16, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16);
        byte[] uuid32 = Arrays.copyOfRange(tailModelByte, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32);
        byte[] head4 = Arrays.copyOfRange(tailModelByte, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32 + TailModel.HEAD_4);
        // 可以copy 0 字节数据
        byte[] fileNameX = Arrays.copyOfRange(tailModelByte, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32 + TailModel.HEAD_4, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32 + TailModel.HEAD_4 + tailModelByte.length - TailModel.MIN_LENGTH);
        byte[] originSize8 = Arrays.copyOfRange(tailModelByte, tailModelByte.length - TailModel.TAIL_FLAG_16 - TailModel.ORIGIN_SIZE_8, tailModelByte.length - TailModel.TAIL_FLAG_16);
        byte[] tailFlag16 = Arrays.copyOfRange(tailModelByte, tailModelByte.length - TailModel.TAIL_FLAG_16, tailModelByte.length);

        model.setBelongUserMd516(userMd5);
        model.setEncodeType16(encodeType16);
        byte[] originUuid32 = EncryptUtil.decryptUuid(PasswordService.getMd545ForUuidEncrypt(), uuid32);
        model.setUuid32(originUuid32);
        model.setHead4(EncryptUtil.decryptContent(originUuid32, PasswordService.getMd523ForContentEncrypt(), head4));
        model.setFileNameX(EncryptUtil.decryptContent(originUuid32, PasswordService.getMd523ForContentEncrypt(), fileNameX));
        model.setOriginTextSize8(originSize8);
        model.setTailFlag16(tailFlag16);

        return model;
    }

    /**
     * 是否是当前用户
     */
    public static boolean isCurrentUser(byte[] belongUserMd516, byte[] md51ForFileAuthentication) {
        return Arrays.equals(belongUserMd516, md51ForFileAuthentication);
    }
}
