package com.qzw.demo.java.filemask.absclass;

import com.qzw.demo.java.filemask.enums.DirChooseEnum;
import com.qzw.demo.java.filemask.enums.FileEncoderTypeEnum;
import com.qzw.demo.java.filemask.enums.MaskEnum;
import com.qzw.demo.java.filemask.exception.MaskException;
import com.qzw.demo.java.filemask.interfaces.FileEncoderType;
import com.qzw.demo.java.filemask.interfaces.PasswordHandler;
import com.qzw.demo.java.filemask.interfaces.PrivateDataAccessor;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BG388892
 * @date 2020/1/18
 */
@Log4j2
public abstract class AbstractFileEncoder implements PasswordHandler, FileEncoderType {

    protected abstract void encodeFile(File fileOrDir, DirChooseEnum chooseEnum);

    protected abstract void decodeFile(File fileOrDir, DirChooseEnum chooseEnum);


    protected boolean isFileMaskFile(File file) {
        boolean fileMask = file.getPath().contains("\\.fileMask");
        if (fileMask) {
            return true;
        }
        if (file.getName().startsWith("nDDiR") || file.getName().startsWith("nDFiLe")) {
            return true;
        }

        return false;


    }

    protected byte[] xorBySecretKey(byte[] text) {
        byte[] byte32 = this.get32byteMd5Value();
        for (int i = 0; i < text.length; i++) {
            text[i] = (byte) (text[i] ^ byte32[i % (byte32.length)]);
        }
        return text;
    }

    protected byte xorBySecretKey4OneByte(byte bt, int index) {
        byte[] byte32 = this.get32byteMd5Value();
        return (byte) (byte32[index] ^ bt);
    }

    protected List<FileEncoderTypeEnum> fileEncodeTypeCheck(File fileOrDir) {
        List<FileEncoderTypeEnum> list = new ArrayList<>();
        mkPrivateDirIfNotExists(fileOrDir);
        String privateDataFile;
        privateDataFile = fileOrDir.getParent() + File.separatorChar + ".fileMask" + File.separatorChar + fileOrDir.getName();

        try (RandomAccessFile raf = new RandomAccessFile(new File(privateDataFile), "rw")) {
            raf.seek(0);

            byte[] bytes = new byte[32];

            raf.read(bytes);

            //todo 权限
            // todo 加密问题
//            bytes = xorBySecretKey(bytes);
            if (bytes[16] == 0x01) {
                list.add(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE);
            }

            if (bytes[16 + 1] == 0x01) {
                list.add(FileEncoderTypeEnum.FILE_HEADER_ENCODE);
            }

            if (bytes[16 + 2] == 0x01) {
                list.add(FileEncoderTypeEnum.FILE_CONTENT_ENCODE);
            }
        } catch (IOException e) {
            // todo or 不处理
        }
        return list;
    }

    protected boolean encodePermit(File fileOrDir) {
        List<FileEncoderTypeEnum> encodedBy = fileEncodeTypeCheck(fileOrDir);
        boolean encodeable = isEncodeable(encodedBy);
        if (!encodeable) {
            return false;
        }
        if (isFileMaskFile(fileOrDir)) {
            // log
            return false;
        }
        return true;
    }

    protected boolean decodePermit(File fileOrDir, FileEncoderTypeEnum fileEncoderTypeEnum) {
        List<FileEncoderTypeEnum> encodedBy = fileEncodeTypeCheck(fileOrDir);
        boolean contains = encodedBy.contains(fileEncoderTypeEnum);
        return contains;
    }


    /**
     * @param fileOrDir 原始文件
     * @return 判断文件是否被其他人以任何方式加密过
     * true : 当前操作者有权限
     * false: 当前操作者没有权限
     */
    protected boolean isOwner(File fileOrDir) {
        String privateDataDirStr = fileOrDir.getParent() + File.separatorChar + ".fileMask";
        // 文件一定存在
        File privateDataDir = new File(privateDataDirStr);
        if (!privateDataDir.exists()) {
            return true;
        }
        String privateDataFile;
        privateDataFile = fileOrDir.getParent() + File.separatorChar + ".fileMask" + File.separatorChar + fileOrDir.getName();

        File file = new File(privateDataFile);
        if (!file.exists()) {
            return true;
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.seek(0);
            byte[] bytes = new byte[16];
            raf.read(bytes);
            //todo 权限
            bytes = xorBySecretKey(bytes);
            boolean isCurrentUser = isCurrentUser(bytes);
            if (isCurrentUser) {
                return true;
            }
            return false;
        } catch (IOException e) {
            // todo or 不处理
        }
        //IO exception 直接不给权限, 一般情况下不会的
        return false;
    }

    /**
     * @param bytes 16字节,解密后的
     * @return
     */
    protected boolean isCurrentUser(byte[] bytes) {
        String s1 = new String(bytes);
        byte[] md5 = getMd5();
        String s2 = new String(md5);
        if (s1.equals(s2)) {
            return true;
        } else {
            return false;
        }
    }

    ;

    /**
     * private date 暂时去掉加密内容
     *
     * @param byteList        加密方式1: 不定长字节
     *                        加密方式2: 32字节
     *                        加密方式3: 256字节
     * @param fileOrDir       原始文件
     * @param encoderTypeEnum
     */
    protected void logPrivateData(byte[] byteList, File fileOrDir, FileEncoderTypeEnum encoderTypeEnum) {
//        mkPrivateDirIfNotExists(fileOrDir);
        String privateDataFile = fileOrDir.getParent() + File.separatorChar + ".fileMask" + File.separatorChar + fileOrDir.getName();
        File file = new File(privateDataFile);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // 头部数据 ,md5值是需要加密的
            byte[] md5 = getMd5();
            byte[] encodedBytes = new byte[16];
            //xor 后没有变化 todo 加密解密
//            encodedBytes = xorBySecretKey(md5);
            raf.write(md5);
            // 私有数据
            if (encoderTypeEnum.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
                raf.seek(32 + 32 + 256);
//                byteList = xorBySecretKey(byteList);
                raf.write(byteList);

                raf.seek(16 + 0);
                byte[] bt = new byte[1];
//                bt[0] = xorBySecretKey4OneByte((byte) 0x01, 16);
                //todo 加密解密
                bt[0] = (byte) 0x01;

                raf.write(bt);

            } else if (encoderTypeEnum.equals(FileEncoderTypeEnum.FILE_HEADER_ENCODE)) {
                raf.seek(32);
//                byteList = xorBySecretKey(byteList);
                raf.write(byteList);

                raf.seek(16 + 1);
                byte[] bt = new byte[1];
                //todo 加密解密
//                bt[0] = xorBySecretKey4OneByte((byte) 0x01, 16 + 1);
                bt[0] = (byte) 0x01;
                raf.write(bt);
            } else if (encoderTypeEnum.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
                raf.seek(32+32);
//                byteList = xorBySecretKey(byteList);
                raf.write(byteList);

                raf.seek(16 + 2);
                byte[] bt = new byte[1];
//                bt[0] = xorBySecretKey4OneByte((byte) 0x01, 16 + 2);
                //todo 加密解密
                bt[0] = (byte) 0x01;
                raf.write(bt);
            }
        } catch (IOException e) {
            // todo 写失败直接删除 或者不处理
        }

    }

    /**
     * @param fileOrDir       required
     * @param encoderTypeEnum
     * @return
     */
    protected byte[] retrievePrivateData(File fileOrDir, FileEncoderTypeEnum encoderTypeEnum) {
        String privateDataFile = fileOrDir.getParent() + File.separatorChar + ".fileMask" + File.separatorChar + fileOrDir.getName();
        File file = new File(privateDataFile);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // construct encoded bytes
            Long length = raf.length();
            raf.seek(0);
            byte[] encodedText = new byte[length.intValue()];
            raf.read(encodedText);
            byte[] decodedText = new byte[encodedText.length];
            // todo 加密解密
            //            decodedText = xorBySecretKey(encodedText);
            decodedText = encodedText;

            byte[] sign = new byte[16];
            System.arraycopy(encodedText, 0, sign, 0, 16);

            boolean isCurrentUser = isCurrentUser(sign);
            if (!isCurrentUser) {
                throw new MaskException(MaskEnum.USER_INVALID);
            }

            byte[] result = null;
            // 私有数据
            if (encoderTypeEnum.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
                result = new byte[length.intValue() - 32 - 32 - 256];

                System.arraycopy(decodedText, 32 + 32 + 256, result, 0, result.length);

            } else if (encoderTypeEnum.equals(FileEncoderTypeEnum.FILE_HEADER_ENCODE)) {
                result = new byte[32];
                System.arraycopy(decodedText, 32, result, 0, 32);
            } else if (encoderTypeEnum.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
                result = new byte[256];
                System.arraycopy(decodedText, 32 + 32, result, 0, 256);
            }
            return result;
        } catch (IOException e) {
            // todo 或者不处理
        }
        return new byte[0];
    }

    protected void mkPrivateDirIfNotExists(File fileOrDir) {
        String privateDataDirStr = fileOrDir.getParent() + File.separatorChar + ".fileMask";
        File file = new File(privateDataDirStr);
        if (!file.exists()) {
            file.mkdir();
            try {
                Runtime.getRuntime().exec("attrib " + "\"" + file.getAbsolutePath() + "\"" + " +H");
            } catch (IOException e) {
                // fileMask dir is not hid that has no effect
            }
        }
    }


    protected boolean isEncodeable(List<FileEncoderTypeEnum> encodedTypeList) {
        // 互斥处理, todo 暂时不考虑方式1和方式2兼容的问题
        if (encodedTypeList != null) {
            for (FileEncoderTypeEnum fileEncoderTypeEnum : encodedTypeList) {
                //加密方式相同
                if (fileEncoderTypeEnum.equals(getFileEncoderType())) {
                    //todo log 文件或文件夹已使用相同方式加密过, 不允许重复加密
                    return false;
                }
                //加密方式为2
                if (FileEncoderTypeEnum.FILE_HEADER_ENCODE.equals(getFileEncoderType())) {
                    if (fileEncoderTypeEnum.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
                        //todo log 文件已使用方式3加密, 不再使用方式2加密
                        return false;
                    }
                }
                //加密方式为3
                if (FileEncoderTypeEnum.FILE_CONTENT_ENCODE.equals(getFileEncoderType())) {
                    if (fileEncoderTypeEnum.equals(FileEncoderTypeEnum.FILE_HEADER_ENCODE)) {
                        //todo log 文件已使用方式2加密, 不再使用方式3加密
                        return false;
                    }
                }
            }
        }
        return true;
    }


    protected void removeEncodeFlag(File fileOrDir, FileEncoderTypeEnum encoderTypeEnum) {
        String privateDataFile = fileOrDir.getParent() + File.separatorChar + ".fileMask" + File.separatorChar + fileOrDir.getName();

        File file = new File(privateDataFile);

        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            // 私有数据
            if (encoderTypeEnum.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {

                raf.seek(16 + 0);
                byte[] bt = new byte[1];
//                bt[0] = xorBySecretKey4OneByte((byte) 0x00, 16 + 0);
                bt[0] = (byte) 0x00;
                raf.write(bt);
            } else if (encoderTypeEnum.equals(FileEncoderTypeEnum.FILE_HEADER_ENCODE)) {
                raf.seek(16 + 1);
                byte[] bt = new byte[1];
//                bt[0] = xorBySecretKey4OneByte((byte) 0x00, 16 + 1);
                bt[0] = (byte) 0x00;

                raf.write(bt);
            } else if (encoderTypeEnum.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
                raf.seek(16 + 2);
                byte[] bt = new byte[1];
//                bt[0] = xorBySecretKey4OneByte((byte) 0x00, 16 + 2);
                bt[0] = (byte) 0x00;

                raf.write(bt);
            }
        } catch (IOException e) {
            // todo 或者不处理
            log.info("文件被占用,打开失败,{}", fileOrDir.getPath());
        }
    }
}
