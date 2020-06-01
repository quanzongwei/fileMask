package com.qzw.filemask.gui;

import com.qzw.filemask.FileMaskMain;
import com.qzw.filemask.enums.ChooseTypeEnum;
import com.qzw.filemask.fileencoder.AbstractFileEncoder;
import com.qzw.filemask.fileencoder.FileContentEncoder;
import com.qzw.filemask.fileencoder.FileHeaderEncoder;
import com.qzw.filemask.fileencoder.FileOrDirNameEncoder;
import com.qzw.filemask.service.WorkFlowService;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * @author quanzongwei
 * @date 2020/2/24
 */
@Log4j2
public class ButtonActionFactory {

    private static JFileChooser jFileChooser = new JFileChooser();

    /**
     * 加密类型一(文件名称加密):文件夹级联加密
     */
    public static void btn11(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileOrDirNameEncoder(), ChooseTypeEnum.CASCADE_DIR);
        });
    }


    /**
     * 加密类型一(文件名称加密):文件夹加密
     */
    public static void btn12(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileOrDirNameEncoder(), ChooseTypeEnum.CURRENT_DIR_ONLY);
        });
    }

    /**
     * 加密类型一(文件名称加密):文件加密
     */
    public static void btn13(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.FILES_ONLY, "请选择文件...");
            doEncrypt(targetDir, new FileOrDirNameEncoder(), ChooseTypeEnum.FILE_ONLY);
        });
    }

    /**
     * 加密类型二(文件头部加密):文件夹级联加密
     */
    public static void btn21(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileHeaderEncoder(), ChooseTypeEnum.CASCADE_DIR);
        });
    }

    /**
     * 加密类型二(文件头部加密):文件夹加密
     */
    public static void btn22(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileHeaderEncoder(), ChooseTypeEnum.CURRENT_DIR_ONLY);
        });
    }

    /**
     * 加密类型二(文件头部加密):文件加密
     */
    public static void btn23(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.FILES_ONLY, "请选择文件...");
            doEncrypt(targetDir, new FileHeaderEncoder(), ChooseTypeEnum.FILE_ONLY);
        });
    }

    /**
     * 加密类型三(文件内容加密):文件夹级联加密
     */
    public static void btn31(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileContentEncoder(), ChooseTypeEnum.CASCADE_DIR);
        });
    }

    /**
     * 加密类型三(文件内容加密):文件夹加密
     */
    public static void btn32(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileContentEncoder(), ChooseTypeEnum.CURRENT_DIR_ONLY);
        });
    }

    /**
     * 加密类型三(文件内容加密):文件加密
     */
    public static void btn33(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.FILES_ONLY, "请选择文件...");
            doEncrypt(targetDir, new FileContentEncoder(), ChooseTypeEnum.FILE_ONLY);
        });
    }

    /**
     * 加密
     */
    private static void doEncrypt(String targetFileOrDir, AbstractFileEncoder fileEncoder, ChooseTypeEnum chooseTypeEnum) {
        WorkFlowService.doEncryptOrDecrypt(targetFileOrDir, fileEncoder, chooseTypeEnum, true);
    }

    /**
     * 解密:文件夹级联解密
     */
    public static void btn41(JButton button) {
        button.addActionListener(e -> {
            String targetFileOrDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doDecrypt(targetFileOrDir, ChooseTypeEnum.CASCADE_DIR);
        });
    }

    /**
     * 解密:文件夹解密
     */
    public static void btn42(JButton button) {
        button.addActionListener(e -> {
            String targetFileOrDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doDecrypt(targetFileOrDir, ChooseTypeEnum.CURRENT_DIR_ONLY);
        });
    }

    /**
     * 解密:文件解密
     */
    public static void btn43(JButton button) {
        button.addActionListener(e -> {
            String targetFileOrDir = directoryAndFileChoose(JFileChooser.FILES_ONLY, "请选择文件...");
            doDecrypt(targetFileOrDir, ChooseTypeEnum.FILE_ONLY);
        });
    }

    private static void doDecrypt(String targetFileOrDir, ChooseTypeEnum chooseTypeEnum) {
        WorkFlowService.doEncryptOrDecrypt(targetFileOrDir, null, chooseTypeEnum, false);
    }

    /**
     * GUI文件选择组件
     */
    private static String directoryAndFileChoose(int fileSelectMode, String message) {
        //选择文件夹
        JFileChooser fileChooser = jFileChooser;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        File tmpDir = new File(fsv.getDefaultDirectory().getPath() + File.separatorChar + "fileMask");
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        fileChooser.setCurrentDirectory(tmpDir);
        fileChooser.setDialogTitle(message);
        fileChooser.setApproveButtonText("确定");
        fileChooser.setFileSelectionMode(fileSelectMode);
        int result = fileChooser.showOpenDialog(null);
        if (JFileChooser.APPROVE_OPTION == result) {
            String path = fileChooser.getSelectedFile().getPath();
            fileChooser.setEnabled(false);
            return path;
        } else if (JFileChooser.CANCEL_OPTION == result) {
            return null;
        } else {
            return null;
        }
    }
}
