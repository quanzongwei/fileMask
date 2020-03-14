package com.qzw.demo.java.filemask;

import com.qzw.demo.java.filehide.FileHideException;
import com.qzw.demo.java.filemask.enums.DirChooseEnum;
import com.qzw.demo.java.filemask.fileencoder.FileContentEncoderV2;
import com.qzw.demo.java.filemask.fileencoder.FileHeaderEncoderV2;
import com.qzw.demo.java.filemask.fileencoder.FileOrDirNameEncoderV2;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author BG388892
 * @date 2020/2/24
 */
@Log4j2
public class ButtonActionFactory {

    static JLabel label = FileHideGUIV7.label;
    static JTextArea ta = FileHideGUIV7.ta;

    static JDialog dialog = FileHideGUIV7.dialog;
    static JFrame f = FileHideGUIV7.f;


    public static void btn11(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("加密路径太短, 请重新选择则!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    new FileOrDirNameEncoderV2().encodeFileOrDir(new File(targetDir), DirChooseEnum.CASCADE_DIR);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("加密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("加密成功!");
                ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");
                dialog.setVisible(true);
            }
        });
    }

    public static void btn12(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("加密路径太短, 请重新选择则!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    new FileOrDirNameEncoderV2().encodeFileOrDir(new File(targetDir), DirChooseEnum.CURRENT_DIR_ONLY);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("加密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("加密成功!");
                ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");

                dialog.setVisible(true);

            }
        });
    }

    public static void btn13(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.FILES_ONLY, "请选择文件...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("加密路径太短, 请重新选择则!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    new FileOrDirNameEncoderV2().encodeFileOrDir(new File(targetDir), DirChooseEnum.FILE_ONLY);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("加密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("加密成功!");
                ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");

                dialog.setVisible(true);

            }
        });
    }

    public static void btn21(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("加密路径太短, 请重新选择则!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    new FileHeaderEncoderV2().encodeFileOrDir(new File(targetDir), DirChooseEnum.CASCADE_DIR);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("加密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("加密成功!");
                ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");

                dialog.setVisible(true);

            }
        });
    }

    public static void btn22(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("加密路径太短, 请重新选择则!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    new FileHeaderEncoderV2().encodeFileOrDir(new File(targetDir), DirChooseEnum.CURRENT_DIR_ONLY);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("加密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("加密成功!");
                ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");

                dialog.setVisible(true);

            }
        });
    }

    public static void btn23(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.FILES_ONLY, "请选择文件...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("加密路径太短, 请重新选择则!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    new FileHeaderEncoderV2().encodeFileOrDir(new File(targetDir), DirChooseEnum.FILE_ONLY);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("加密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("加密成功!");
                ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");

                dialog.setVisible(true);

            }
        });
    }

    public static void btn31(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("加密路径太短, 请重新选择则!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    new FileContentEncoderV2().encodeFileOrDir(new File(targetDir), DirChooseEnum.CASCADE_DIR);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("加密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("加密成功!");
                ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");

                dialog.setVisible(true);

            }
        });
    }

    public static void btn32(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("加密路径太短, 请重新选择则!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    new FileContentEncoderV2().encodeFileOrDir(new File(targetDir), DirChooseEnum.CURRENT_DIR_ONLY);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("加密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("加密成功!");
                ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");

                dialog.setVisible(true);

            }
        });
    }

    public static void btn33(JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.FILES_ONLY, "请选择文件...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("加密路径太短, 请重新选择则!");
                    dialog.setVisible(true);
                    return;
                }
                // 加密软件运行中
                try {
                    new FileContentEncoderV2().encodeFileOrDir(new File(targetDir), DirChooseEnum.FILE_ONLY);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("加密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("加密成功!");
                ta.append("加密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");

                dialog.setVisible(true);
            }
        });
    }

    public static void btn41(JButton button) {

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("解密路径不合法, 请重新选择!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    // 解密顺序很重要啊, 如果先使用方式一解密, 那么文件名称被修改, 使用文件二解密会抛出文件不存在异常
                    //方式三
                    new FileContentEncoderV2().decodeFileOrDir(new File(targetDir), DirChooseEnum.CASCADE_DIR);
                    //方式二
                    new FileHeaderEncoderV2().decodeFileOrDir(new File(targetDir), DirChooseEnum.CASCADE_DIR);
                    //方式一
                    new FileOrDirNameEncoderV2().decodeFileOrDir(new File(targetDir), DirChooseEnum.CASCADE_DIR);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("解密异常", ex);
                    ta.append(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("解密成功!");
                ta.append("解密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");
                dialog.setVisible(true);
            }
        });
    }

    public static void btn42(JButton button) {

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.DIRECTORIES_ONLY, "请选择文件夹...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("解密路径不合法, 请重新选择!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    //方式三
                    new FileContentEncoderV2().decodeFileOrDir(new File(targetDir), DirChooseEnum.CURRENT_DIR_ONLY);
                    //方式二
                    new FileHeaderEncoderV2().decodeFileOrDir(new File(targetDir), DirChooseEnum.CURRENT_DIR_ONLY);
                    //方式一
                    new FileOrDirNameEncoderV2().decodeFileOrDir(new File(targetDir), DirChooseEnum.CURRENT_DIR_ONLY);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("解密异常", ex);
                    ta.append(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("解密成功!");
                ta.append("解密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);
                ta.append("\r\n");
                dialog.setVisible(true);
            }
        });
    }

    public static void btn43(JButton button) {

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long begin = System.currentTimeMillis();
                String targetDir = directoryAndFileChooseV2(JFileChooser.FILES_ONLY, "请选择文件...");
                if (targetDir == null) {
                    // 用户点击取消按钮, 取消加密
                    return;
                }
                if (!isValidPath(targetDir)) {
                    label.setText("解密路径不合法, 请重新选择!");
                    dialog.setVisible(true);
                    return;
                }
                try {
                    //方式三
                    new FileContentEncoderV2().decodeFileOrDir(new File(targetDir), DirChooseEnum.FILE_ONLY);
                    //方式二
                    new FileHeaderEncoderV2().decodeFileOrDir(new File(targetDir), DirChooseEnum.FILE_ONLY);
                    //方式一
                    new FileOrDirNameEncoderV2().decodeFileOrDir(new File(targetDir), DirChooseEnum.FILE_ONLY);
                } catch (FileHideException ex) {
                    label.setText(ex.getMessage());
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("解密异常", ex);
                    ta.append(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }
                label.setText("解密成功!");
                ta.append("解密成功,耗时:" + (System.currentTimeMillis() - begin) + "ms; 文件名: " + targetDir);

                ta.append("\r\n");
                dialog.setVisible(true);
            }
        });
    }

    public static JFileChooser jFileChooser = new JFileChooser();

    public static String directoryAndFileChooseV2(int fileSelectMode, String message) {
        //选择文件夹
        JFileChooser fileChooser = jFileChooser;
        FileSystemView fsv = FileSystemView.getFileSystemView();
        System.out.println(fsv.getHomeDirectory());
//        fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
//        fsv.getDefaultDirectory();
        File testDir = new File(fsv.getDefaultDirectory().getPath() + File.separatorChar + "fileMask");
        if (!testDir.exists()) {
            testDir.mkdir();
            // todo 隐藏
        }
        testDir = new File("D:\\Data\\测试");
        fileChooser.setCurrentDirectory(testDir);
        fileChooser.setDialogTitle(message);
        fileChooser.setApproveButtonText("确定");
        fileChooser.setFileSelectionMode(fileSelectMode);
        int result = fileChooser.showOpenDialog(null);
        if (JFileChooser.APPROVE_OPTION == result) {
            String path = fileChooser.getSelectedFile().getPath();
            System.out.println("path: " + path);
            fileChooser.setEnabled(false);
            return path;
        } else if (JFileChooser.CANCEL_OPTION == result) {
            return null;
        } else {
            return null;
        }
    }


    public static boolean isValidPath(String targetPath) {
        if (targetPath.length() <= 3) {
            return false;
        }
        return true;
    }


}
