package com.qzw.filemask;

import com.qzw.filemask.enums.ChooseTypeEnum;
import com.qzw.filemask.fileencoder.AbstractFileEncoder;
import com.qzw.filemask.fileencoder.FileContentEncoder;
import com.qzw.filemask.fileencoder.FileHeaderEncoder;
import com.qzw.filemask.fileencoder.FileOrDirNameEncoder;
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

    private static JTextArea ta = FileMaskMain.ta;
    private static JFrame f = FileMaskMain.f;
    private static JFileChooser jFileChooser = new JFileChooser();

    public static void btn11(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileOrDirNameEncoder(), ChooseTypeEnum.CASCADE_DIR);
        });
    }


    public static void btn12(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileOrDirNameEncoder(), ChooseTypeEnum.CURRENT_DIR_ONLY);
        });
    }

    public static void btn13(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.FILES_ONLY, "请选择文件...");
            doEncrypt(targetDir, new FileOrDirNameEncoder(), ChooseTypeEnum.FILE_ONLY);
        });
    }

    public static void btn21(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileHeaderEncoder(), ChooseTypeEnum.CASCADE_DIR);
        });
    }

    public static void btn22(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileHeaderEncoder(), ChooseTypeEnum.CURRENT_DIR_ONLY);
        });
    }

    public static void btn23(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.FILES_ONLY, "请选择文件...");
            doEncrypt(targetDir, new FileHeaderEncoder(), ChooseTypeEnum.FILE_ONLY);
        });
    }

    public static void btn31(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileContentEncoder(), ChooseTypeEnum.CASCADE_DIR);
        });
    }

    public static void btn32(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doEncrypt(targetDir, new FileContentEncoder(), ChooseTypeEnum.CURRENT_DIR_ONLY);
        });
    }

    public static void btn33(JButton btn) {
        btn.addActionListener(e -> {
            String targetDir = directoryAndFileChoose(JFileChooser.FILES_ONLY, "请选择文件...");
            doEncrypt(targetDir, new FileContentEncoder(), ChooseTypeEnum.FILE_ONLY);
        });
    }

    private static void doEncrypt(String targetFileOrDir, AbstractFileEncoder fileEncoder, ChooseTypeEnum chooseTypeEnum) {
        long begin = System.currentTimeMillis();
        if (targetFileOrDir == null) {
            // 用户点击取消按钮, 取消加密
            return;
        }
        if (!isValidPath(targetFileOrDir)) {
            JOptionPane.showConfirmDialog(f, "加密路径太短, 请重新选择则!", "提示", JOptionPane.DEFAULT_OPTION);
            return;
        }
        try {
            fileEncoder.encodeFileOrDir(new File(targetFileOrDir), chooseTypeEnum);
        } catch (Exception ex) {
            log.info("加密异常", ex);
            ta.append("加异异常:" + ex.getMessage() + "\r\n");
            JOptionPane.showConfirmDialog(f, "加密出错!!!", "提示", JOptionPane.DEFAULT_OPTION);
            return;
        }
        ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetFileOrDir + "\r\n");
        JOptionPane.showConfirmDialog(f, "加密成功!", "提示", JOptionPane.DEFAULT_OPTION);
    }

    public static void btn41(JButton button) {
        button.addActionListener(e -> {
            String targetFileOrDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doDecrypt(targetFileOrDir, ChooseTypeEnum.CASCADE_DIR);
        });
    }

    public static void btn42(JButton button) {
        button.addActionListener(e -> {
            String targetFileOrDir = directoryAndFileChoose(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
            doDecrypt(targetFileOrDir, ChooseTypeEnum.CURRENT_DIR_ONLY);
        });
    }

    public static void btn43(JButton button) {
        button.addActionListener(e -> {
            String targetFileOrDir = directoryAndFileChoose(JFileChooser.FILES_ONLY, "请选择文件...");
            doDecrypt(targetFileOrDir, ChooseTypeEnum.FILE_ONLY);
        });
    }

    private static void doDecrypt(String targetFileOrDir, ChooseTypeEnum chooseTypeEnum) {
        long begin = System.currentTimeMillis();
        if (targetFileOrDir == null) {
            // 用户点击取消按钮, 取消加密
            return;
        }
        if (!isValidPath(targetFileOrDir)) {
            JOptionPane.showConfirmDialog(f, "解密路径不合法, 请重新选择!", "提示", JOptionPane.DEFAULT_OPTION);
            return;
        }
        try {
            //方式三
            new FileContentEncoder().decodeFileOrDir(new File(targetFileOrDir), chooseTypeEnum);
            //方式二
            new FileHeaderEncoder().decodeFileOrDir(new File(targetFileOrDir), chooseTypeEnum);
            //方式一
            new FileOrDirNameEncoder().decodeFileOrDir(new File(targetFileOrDir), chooseTypeEnum);
        } catch (Exception ex) {
            log.info("解密出错", ex);
            ta.append("解密出错:" + ex.getMessage() + "\r\n");
            JOptionPane.showConfirmDialog(f, "加密出错!!!", "提示", JOptionPane.DEFAULT_OPTION);
            return;
        }
        ta.append("解密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetFileOrDir + "\r\n");
        JOptionPane.showConfirmDialog(f, "解密成功!", "提示", JOptionPane.DEFAULT_OPTION);
    }

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

    private static boolean isValidPath(String targetPath) {
        if (targetPath.length() <= 3) {
            return false;
        }
        return true;
    }
}
