package com.qzw.filemask.fileencoder;

import com.qzw.filemask.enums.FileEncoderTypeEnum;
import com.qzw.filemask.interfaces.PasswordHandler;
import com.qzw.filemask.util.ByteUtil;
import com.qzw.filemask.util.PrivateDataUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**
 * 文件尾部数据结构
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
 * @author BG388892
 * @date 2020/5/13
 */
@Data
@Log4j2
public class TailUtil {
    static String FILE_MASK_TAIL_FLAG = "FileMaskTailFlag";
    static Long minLength = 16 + 16 + 32 + 4 + 8 + 16L;


    static int FileNameEncodeTagPosition = 0;
    static int FileHeadEncodeTagPosition = 1;
    static int FileContentEncodeTagPosition = 2;

    /**
     * 已经加密标志
     */
    static byte ENCODED_FLAG = (byte) 0x01;


    /**
     * 是否存在尾部数据结构
     * 通过: FileMaskTailFlag标识
     */
    private static boolean existsTail(RandomAccessFile raf) throws IOException {
        long length = raf.length();
        if (length < minLength) {
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
     * @param uuid       32位
     * @param md532      32位 由password引申出的值
     * @param originText 需要加密的数据
     * @return 加密后的和原来数据等字节的数据
     */
    private static byte[] encrypt(byte[] uuid, byte[] md532, byte[] originText) {

        byte[] dataEncodeKey = new byte[uuid.length];
        for (int i = 0; i < uuid.length; i++) {
            dataEncodeKey[i] = (byte) (uuid[i] ^ md532[i]);
        }

        byte[] result = new byte[originText.length];


        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (originText[i] ^ dataEncodeKey[i % (dataEncodeKey.length)]);
        }

        return result;
    }

    /**
     * @param uuid          32位
     * @param md532         32位 由password引申出的值
     * @param encryptedText 需要加密的数据
     * @return 解密后的字节数据
     */
    private static byte[] decrypt(byte[] uuid, byte[] md532, byte[] encryptedText) {
        byte[] dataEncodeKey = new byte[uuid.length];
        for (int i = 0; i < uuid.length; i++) {
            dataEncodeKey[i] = (byte) (uuid[i] ^ md532[i]);
        }

        byte[] result = new byte[encryptedText.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (encryptedText[i] ^ dataEncodeKey[i % (dataEncodeKey.length)]);
        }
        return result;
    }

    //生成尾部文件
    private static void firstTimeAppendTailTOFile(RandomAccessFile raf, TailModel model) throws IOException {
        setTail(raf, model, null);
    }

    //设置尾部文件
    private static void replaceFileTail(RandomAccessFile raf, TailModel model) throws IOException {
        setTail(raf, model, ByteUtil.bytesToLong(model.getOriginTextSize8()));
    }

    /**
     * @param raf
     * @param model
     * @param existLength 为null 表示首次设置
     * @throws IOException
     */
    private static void setTail(RandomAccessFile raf, TailModel model, Long existLength) throws IOException {
        long length = existLength == null ? raf.length() : existLength;
        if (existLength == null) {
            raf.seek(raf.length());
        } else {
            raf.setLength(existLength);
            raf.seek(ByteUtil.bytesToLong(model.getOriginTextSize8()));
        }
        raf.write(model.getBelongUserMd516());
        raf.write(model.getEncodeType16());
        raf.write(model.getUuid32());
        raf.write(model.getHead4());
        if (model.getFileNameX() != null) {
            raf.write(model.getFileNameX());
        }
        // todo 这个很重要
        raf.writeLong(length);
        raf.write(model.getTailFlag16());
    }

    /**
     * @param raf
     * @param md51 用户认证,16字节长度
     */
    private static boolean isCurrentUser(RandomAccessFile raf, byte[] md51) throws IOException {
        long textSize = getOriginTextSize(raf);

        raf.seek(textSize);
        byte[] userMd5Bytes = new byte[TailModel.USER_MD5_LENGTH_16];
        raf.read(userMd5Bytes);

        System.out.println(Base64.getEncoder().encodeToString(userMd5Bytes));
        System.out.println(Base64.getEncoder().encodeToString(md51));

        if (Arrays.equals(userMd5Bytes, md51)) {
            return true;
        }
        return false;
    }

    private static long getOriginTextSize(RandomAccessFile raf) throws IOException {
        raf.seek(raf.length() - TailModel.ORIGIN_SIZE_8 - TailModel.TAIL_FLAG_16);
        byte[] tailSizeByte = new byte[TailModel.ORIGIN_SIZE_8];
        raf.read(tailSizeByte);
        return ByteUtil.bytesToLong(tailSizeByte);
    }

    private static boolean isEncryptedByTypeOrConflict(RandomAccessFile raf, FileEncoderTypeEnum fileEncoderType) throws IOException {
        long textSize = getOriginTextSize(raf);
        raf.seek(textSize + TailModel.USER_MD5_LENGTH_16);
        byte[] encodeTypeFlagByte = new byte[TailModel.ENCODE_TYPE_FLAG_16];
        raf.read(encodeTypeFlagByte);

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

    private static void generateTailAndAppendToFile(File fileOrDir, FileEncoderTypeEnum fileEncoderType, RandomAccessFile raf) throws IOException {
        TailModel model = new TailModel();
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        byte[] md51 = PasswordHandler.getMd51ForFileAuthentication();
        model.setBelongUserMd516(md51);
        model.setEncodeType16(new byte[16]);
        model.setUuid32(uuid.getBytes());
        model.setHead4(new byte[4]);
        model.setTailFlag16(TailUtil.FILE_MASK_TAIL_FLAG.getBytes());

        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
            byte[] type16 = new byte[16];
            type16[FileNameEncodeTagPosition] = ENCODED_FLAG;
            model.setEncodeType16(type16);
            model.setFileNameX(TailUtil.encrypt(uuid.getBytes(), PasswordHandler.getMd523ForContentEncrypt(), fileOrDir.getName().getBytes("UTF-8")));
        }

        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_HEADER_ENCODE)) {

            if (raf.length() < 4) {
                log.info("文件数据不足4字节,切换为全文加密,{}", fileOrDir);
                fileEncoderType = FileEncoderTypeEnum.FILE_CONTENT_ENCODE;
            } else {
                raf.seek(0);
                byte[] originHead4 = new byte[4];
                raf.read(originHead4);
                model.setHead4(TailUtil.encrypt(uuid.getBytes(), PasswordHandler.getMd523ForContentEncrypt(), originHead4));
                byte[] type16 = new byte[16];
                type16[FileHeadEncodeTagPosition] = ENCODED_FLAG;
                model.setEncodeType16(type16);
                //执行加密操作
                raf.seek(0);
                raf.writeByte(255);
                raf.writeByte(254);
                raf.writeByte(0);
                raf.writeByte(0);
            }
        }
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
            byte[] type16 = new byte[16];
            type16[FileContentEncodeTagPosition] = ENCODED_FLAG;
            model.setEncodeType16(type16);
            //全文加密
            doFileContentEncryptionOrDecryption(raf, true, uuid, raf.length());
        }

        TailUtil.firstTimeAppendTailTOFile(raf, model);
    }

    static int SIZE_1024 = 1024;

    private static void doFileContentEncryptionOrDecryption(RandomAccessFile raf, boolean isEncodeOperation, String uuid, long originTextLength) throws IOException {
        long length = originTextLength;
        long blockNum = length / SIZE_1024;
        Long remain = length % SIZE_1024;
        for (long i = 0; i < blockNum; i++) {
            byte[] b1 = new byte[SIZE_1024];
            raf.seek(0 + i * SIZE_1024);
            raf.read(b1, 0, SIZE_1024);
            if (isEncodeOperation) {
                b1 = encrypt(uuid.getBytes(), PasswordHandler.getMd523ForContentEncrypt(), b1);
            } else {
                b1 = decrypt(uuid.getBytes(), PasswordHandler.getMd523ForContentEncrypt(), b1);
            }
            raf.seek(0 + i * SIZE_1024);
            raf.write(b1);
        }
        // 尾部数据处理
        if (remain > 0) {
            byte[] b3 = new byte[remain.intValue()];
            raf.seek(0 + blockNum * SIZE_1024);
            raf.read(b3, 0, remain.intValue());
            if (isEncodeOperation) {
                b3 = decrypt(uuid.getBytes(), PasswordHandler.getMd523ForContentEncrypt(), b3);
            } else {
                b3 = decrypt(uuid.getBytes(), PasswordHandler.getMd523ForContentEncrypt(), b3);
            }
            raf.seek(0 + blockNum * SIZE_1024);
            raf.write(b3);
        }
    }


    public static void main(String[] args) {
        byte[] a = new byte[10];
        byte[] bytes = Arrays.copyOfRange(a, 1, 0);
        System.out.println(bytes.length);
    }

    /**
     * 去除加密
     *
     * @param raf
     * @return 调用这个方法之前, 一定是存在的
     * @throws IOException
     */
    private static TailModel getExistsTailModelInfo(RandomAccessFile raf) throws IOException {

        TailModel model = new TailModel();
        long originTextSize = getOriginTextSize(raf);
        raf.seek(originTextSize);
        System.out.println(raf.length() + "raf.length");
        System.out.println(originTextSize + "originTextSize");
        byte[] tailModelByte = new byte[Math.toIntExact(raf.length() - originTextSize)];
        raf.read(tailModelByte);

        byte[] userMd5 = Arrays.copyOfRange(tailModelByte, 0, TailModel.USER_MD5_LENGTH_16);
        byte[] encodeType16 = Arrays.copyOfRange(tailModelByte, 0 + TailModel.USER_MD5_LENGTH_16, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16);
        byte[] uuid32 = Arrays.copyOfRange(tailModelByte, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32);
        byte[] head4 = Arrays.copyOfRange(tailModelByte, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32 + TailModel.HEAD_4);
        // 可以copy 0 字节数据
        byte[] fileNameX = Arrays.copyOfRange(tailModelByte, 0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32 + TailModel.HEAD_4, (int) (0 + TailModel.USER_MD5_LENGTH_16 + TailModel.ENCODE_TYPE_FLAG_16 + TailModel.UUID_32 + TailModel.HEAD_4 + tailModelByte.length - minLength));

        byte[] originSize8 = Arrays.copyOfRange(tailModelByte, tailModelByte.length - TailModel.TAIL_FLAG_16 - TailModel.ORIGIN_SIZE_8, tailModelByte.length - TailModel.TAIL_FLAG_16);
        byte[] tailFlag16 = Arrays.copyOfRange(tailModelByte, tailModelByte.length - TailModel.TAIL_FLAG_16, tailModelByte.length);

        model.setBelongUserMd516(userMd5);
        model.setEncodeType16(encodeType16);
        model.setUuid32(uuid32);
        model.setHead4(decrypt(uuid32, PasswordHandler.getMd523ForContentEncrypt(), head4));
        model.setFileNameX(decrypt(uuid32, PasswordHandler.getMd523ForContentEncrypt(), fileNameX));
        model.setOriginTextSize8(originSize8);
        model.setTailFlag16(tailFlag16);

        return model;

    }

    //todo
    private static void doEncryptByTypeWhenTailHasInitialized(File fileOrDir, RandomAccessFile raf, FileEncoderTypeEnum fileEncoderType) throws IOException {

        TailModel model = getExistsTailModelInfo(raf);
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
            byte[] type16 = model.getEncodeType16();
            type16[FileNameEncodeTagPosition] = ENCODED_FLAG;
            model.setEncodeType16(type16);
            model.setFileNameX(TailUtil.encrypt(model.getUuid32(), PasswordHandler.getMd523ForContentEncrypt(), fileOrDir.getName().getBytes("UTF-8")));
        }
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_HEADER_ENCODE)) {
            if (raf.length() < 4) {
                log.info("文件数据长度不足4字节,切换为全文加密,{}", fileOrDir);
                fileEncoderType = FileEncoderTypeEnum.FILE_CONTENT_ENCODE;
            } else {
                raf.seek(0);
                byte[] originHead4 = new byte[TailModel.HEAD_4];
                raf.read(originHead4);
                model.setHead4(TailUtil.encrypt(model.getUuid32(), PasswordHandler.getMd523ForContentEncrypt(), originHead4));
                byte[] type16 = model.getEncodeType16();
                type16[FileHeadEncodeTagPosition] = ENCODED_FLAG;
                model.setEncodeType16(type16);
                //执行加密操作
                raf.seek(0);
                raf.writeByte(255);
                raf.writeByte(254);
                raf.writeByte(0);
                raf.writeByte(0);
            }

        }
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
            byte[] type16 = model.getEncodeType16();
            type16[FileContentEncodeTagPosition] = ENCODED_FLAG;
            model.setEncodeType16(type16);
            //全文加密
            doFileContentEncryptionOrDecryption(raf, true, new String(model.getUuid32()), ByteUtil.bytesToLong(model.getOriginTextSize8()));
        }

        replaceFileTail(raf, model);

    }

    // 解密所有类型 ,注意文件夹解密,是否支持文件夹加密
    public static void decryptAllType(File fileOrDir) {
        // 文件夹走单独逻辑
        if (fileOrDir.isDirectory()) {
            try {
                encodeOrDecodeFileName(fileOrDir, false);
            } catch (IOException e) {
                log.info("加解文件夹失败,{}", fileOrDir);
            }
            return;
        }

        FileEncoderTypeEnum fileEncoderType = null;
        String targetPath = null;
        try (RandomAccessFile raf = new RandomAccessFile(fileOrDir, "rw")) {
            if (!existsTail(raf)) {
                log.info("尾部文件不存在,无需解密,{}", fileOrDir);
                return;
            }
            boolean currentUser = isCurrentUser(raf, PasswordHandler.getMd51ForFileAuthentication());
            if (!currentUser) {
                log.info("文件用户认证失败,解密操作不成功,{}", fileOrDir.getPath());
                return;
            }
            TailModel model = getExistsTailModelInfo(raf);

            byte[] encodeType16 = model.getEncodeType16();
            if (encodeType16[FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE.getPosition()] == ENCODED_FLAG) {
                targetPath = fileOrDir.getParent() + File.separator + new String(model.getFileNameX(), "UTF-8");
            }
            if (encodeType16[FileEncoderTypeEnum.FILE_HEADER_ENCODE.getPosition()] == ENCODED_FLAG) {
                raf.seek(0);
                raf.write(model.getHead4());

            } else if (encodeType16[FileEncoderTypeEnum.FILE_CONTENT_ENCODE.getPosition()] == ENCODED_FLAG) {
                //执行解密
                doFileContentEncryptionOrDecryption(raf, false, new String(model.getUuid32()), ByteUtil.bytesToLong(model.getOriginTextSize8()));
            }
            //最后直接删除尾部文件,数据恢复正常
            raf.setLength(ByteUtil.bytesToLong(model.getOriginTextSize8()));
            // 有没有必要同步落到盘中
            // raf.getFD().sync();
        } catch (Exception ex) {
            //如果文件被binaryViewer打开的话,无法执行成功,应该是被占用了
            log.info("文件操作失败,解密操作不成功,{},{}", fileOrDir.getPath(), ex.getMessage());
        }

        //IO操作完成后才可以执行重命名操作
        if (StringUtils.isNotBlank(targetPath)) {
            boolean b = fileOrDir.renameTo(new File(targetPath));
            log.info("文件名称是否解密成功:{}", b);
        }
    }

    public static void encryptByType(File fileOrDir, FileEncoderTypeEnum fileEncoderType) {
        //文件夹加密走单独逻辑
        if (fileOrDir.isDirectory()) {
            if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
                try {
                    encodeOrDecodeFileName(fileOrDir, true);
                } catch (IOException e) {
                    log.info("加密文件夹失败,{}", fileOrDir);
                }
                return;
            }
            return;
        }

        // 流程:尾部数据结构是否存在? 是否存在是当前用户? 是否是使用同一种方式加密? 是否互斥?
        try (RandomAccessFile raf = new RandomAccessFile(fileOrDir, "rw")) {
            boolean existsTail = TailUtil.existsTail(raf);
            //不存在
            if (!existsTail) {
                TailUtil.generateTailAndAppendToFile(fileOrDir, fileEncoderType, raf);
            }
            //存在
            else {
                boolean isCurrentUser = TailUtil.isCurrentUser(raf, PasswordHandler.getMd51ForFileAuthentication());
                if (!isCurrentUser) {
                    log.info("文件认证用户失败,{}", fileOrDir);
                    return;
                }
                boolean hasEncryptedByTypeOrConflict = TailUtil.isEncryptedByTypeOrConflict(raf, fileEncoderType);
                if (hasEncryptedByTypeOrConflict) {
                    log.info("文件已加密,无需重复加密,{}", fileOrDir);
                    return;
                }
                TailUtil.doEncryptByTypeWhenTailHasInitialized(fileOrDir, raf, fileEncoderType);
            }
        } catch (Exception ex) {
            log.info("文件操作失败,加密操作不成功,{}", fileOrDir.getPath());
            return;
        }

        //IO操作完成后才可以执行重命名操作
        if (fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
            Integer sequence = PrivateDataUtils.getAutoIncrementSequence4ParentDir(fileOrDir);
            //私有数据文件重命名
            String targetName = String.valueOf(sequence);
            String targetPath = fileOrDir.getParent() + File.separatorChar + targetName;
            boolean b = fileOrDir.renameTo(new File(targetPath));
            log.info("文件名称是否加密成功:{}", b);
        }
    }

    private static void encodeOrDecodeFileName(File fileOrDir, boolean ifEncodeOperation) throws IOException {
        // 加密
        if (ifEncodeOperation) {
            Integer sequence = PrivateDataUtils.getAutoIncrementSequence4ParentDir(fileOrDir);
            File privateDataFile = PrivateDataUtils.getPrivateDataFileReleaseV2(fileOrDir, sequence);
            if (!privateDataFile.exists()) {
                try {
                    privateDataFile.createNewFile();
                } catch (IOException e) {
                    log.info("创建私有数据文件失败,{}", fileOrDir);
                    return;
                }
            }
            try (RandomAccessFile raf = new RandomAccessFile(privateDataFile, "rw")) {
                generateTailAndAppendToFile(fileOrDir, FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE, raf);
            } catch (Exception ex) {
                log.info("私有数据文件设置重命名数据失败,{}", fileOrDir);
                return;
            }
            boolean success = fileOrDir.renameTo(new File(fileOrDir.getParent() + File.separator + sequence));
            if (!success) {
                log.info("源文件重命名失败,{}", fileOrDir);
                //同时删除私有数据文件
                privateDataFile.delete();
            }
        }
        //解密
        else {
            File privateDataFile = PrivateDataUtils.getPrivateDataFileReleaseV2(fileOrDir, Integer.valueOf(fileOrDir.getName()));
            if (!privateDataFile.exists()) {
                log.info("文件夹未加密,无需加密,{}", fileOrDir);

                //不存在的文件, 没有createNewFie和new RandomAccessFile 操作的话是不会新建的; 所以也不需要删除
                //privateDataFile.delete();

                return;
            }
            TailModel model;
            try (RandomAccessFile raf = new RandomAccessFile(privateDataFile, "rw")) {
                model = getExistsTailModelInfo(raf);
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
}
