package com.qzw.demo.java.filemask.fileencoder;

import com.qzw.demo.java.filemask.absclass.AbstractFileEncoder;
import com.qzw.demo.java.filemask.component.PasswordHolder;
import com.qzw.demo.java.filemask.enums.DirChooseEnum;
import com.qzw.demo.java.filemask.enums.FileEncoderTypeEnum;
import com.qzw.demo.java.filemask.exception.MaskException;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;

import java.io.File;

/**
 * @author BG388892
 * @date 2020/1/18
 */
@Log4j2
public class FileOrDirNameEncoder extends AbstractFileEncoder {
    @Override
    public FileEncoderTypeEnum getFileEncoderType() {
        return FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE;
    }

    public static int sequence = 0;

    /**
     * 加密也需要校验所有者权限
     *
     * @param fileOrDir
     * @param dirChooseEnum
     */
    @Override
    public void encodeFile(File fileOrDir, DirChooseEnum dirChooseEnum) {
        if (!fileOrDir.exists()) {
            throw new MaskException(1000, "文件或者文件夹不存在解密失败");
        }
        // todo 文件不存在校验
        // 暂未发现rename失败的情况
        sequence++;
        if (dirChooseEnum.equals(DirChooseEnum.FILE_ONLY)) {
            //todo sequence 保存问题 ,文件夹必须要有的
            this.mkPrivateDirIfNotExists(fileOrDir);
            if (encodePermit(fileOrDir)) {
                rename(fileOrDir, sequence++);
            }
        } else if (dirChooseEnum.equals(DirChooseEnum.CURRENT_DIR_ONLY)) {

            //todo sequence 保存问题 ,文件夹必须要有的
            this.mkPrivateDirIfNotExists(fileOrDir);


            File[] files = fileOrDir.listFiles();
            if (files != null && files.length > 0) {
                this.mkPrivateDirIfNotExists(files[0]);
                for (File file : files) {
                    if (encodePermit(file)) {
                        rename(file, sequence++);
                    }
                }
            }
            if (encodePermit(fileOrDir)) {
                rename(fileOrDir, sequence++);
            }

        } else if (dirChooseEnum.equals(DirChooseEnum.CASCADE_DIR)) {
            //todo sequence 保存问题 ,文件夹必须要有的
            this.mkPrivateDirIfNotExists(fileOrDir);

            File[] files = fileOrDir.listFiles();
            if (files != null && files.length > 0) {
                this.mkPrivateDirIfNotExists(files[0]);

                for (File file : files) {
                    //cascade directory
                    if (file.isDirectory()) {
                        encodeFile(file, DirChooseEnum.CASCADE_DIR);
                        continue;
                    }
                    if (encodePermit(file)) {
                        rename(file, sequence++);
                    }
                }
            }

            if (encodePermit(fileOrDir)) {
                rename(fileOrDir, sequence++);
            }
        }
    }

    private void rename(File fileOrDir, int sequence) {
        // todo 文件不存在校验

        String oldname = fileOrDir.getName();
        String newName = (fileOrDir.isDirectory() ? "nDDir" : "nDFiLe") + sequence;
        String newPath = fileOrDir.getParent() + File.separatorChar + newName;
        // todo test change 名字还是原来的名字
        boolean b = fileOrDir.renameTo(new File(newPath));
        if (b) {
            logPrivateData(oldname.getBytes(), fileOrDir, getFileEncoderType());
            String newName2 = fileOrDir.getParent() + File.separatorChar + ".fileMask" + File.separatorChar + oldname;
            String newName3 = fileOrDir.getParent() + File.separatorChar + ".fileMask" + File.separatorChar + newName;
            File filePrivateData = new File(newName2);
            filePrivateData.renameTo(new File(newName3));
        } else {
            // log not success
            log.info("重命名失败{}", fileOrDir.getPath());
        }
    }

    /**
     * @param fileOrDir
     * @param originName
     * @return new Path, null means rename failed
     */
    private void derename(File fileOrDir, String originName) {
        String originPath = fileOrDir.getParent() + File.separatorChar + originName;
        boolean b = fileOrDir.renameTo(new File(originPath));
        if (b) {
            removeEncodeFlag(fileOrDir, getFileEncoderType());
            String oldPath = fileOrDir.getParent() + File.separatorChar + ".fileMask" + File.separatorChar + originName;
            String newPath = fileOrDir.getParent() + File.separatorChar + ".fileMask" + File.separatorChar + fileOrDir.getName();
            // private file always renames success
            boolean b1 = new File(newPath).renameTo(new File(oldPath));
        } else {
            //log
        }
    }


    @Override
    public void decodeFile(File fileOrDir, DirChooseEnum dirChooseEnum) {
        if (!fileOrDir.exists()) {
            throw new MaskException(10000, "文件不存在,解密失败");
        }
        //todo 文件不存在校验
        // 暂未发现rename失败的情况
        if (dirChooseEnum.equals(DirChooseEnum.FILE_ONLY)) {
            if (decodePermit(fileOrDir, getFileEncoderType())) {
                derename(fileOrDir, new String(retrievePrivateData(fileOrDir, getFileEncoderType())));
            }
        } else if (dirChooseEnum.equals(DirChooseEnum.CURRENT_DIR_ONLY)) {
            File[] files = fileOrDir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (decodePermit(file, getFileEncoderType())) {
                        derename(file, new String(retrievePrivateData(file, getFileEncoderType())));
                    }
                }
            }
            if (decodePermit(fileOrDir, getFileEncoderType())) {
                derename(fileOrDir, new String(retrievePrivateData(fileOrDir, getFileEncoderType())));
            }
        } else if (dirChooseEnum.equals(DirChooseEnum.CASCADE_DIR)) {
            File[] files = fileOrDir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    //cascade directory
                    if (file.isDirectory()) {
                        decodeFile(file, DirChooseEnum.CASCADE_DIR);
                        continue;
                    }
                    if (decodePermit(file, getFileEncoderType())) {
                        derename(file, new String(retrievePrivateData(file, getFileEncoderType())));
                    }
                }
            }

            if (decodePermit(fileOrDir, getFileEncoderType())) {
                derename(fileOrDir, new String(retrievePrivateData(fileOrDir, getFileEncoderType())));
            }
        }
    }

    @Test
    public void testEncode() {
        PasswordHolder.password = "123456";
        encodeFile(new File("D:\\Data\\测试\\aa"), DirChooseEnum.CASCADE_DIR);
    }

    @Test
    public void testDecode() {
        PasswordHolder.password = "123456";
        decodeFile(new File("D:\\Data\\测试\\nDDir163"), DirChooseEnum.CASCADE_DIR);
    }



}
