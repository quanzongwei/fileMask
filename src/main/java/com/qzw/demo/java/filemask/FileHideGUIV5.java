package com.qzw.demo.java.filemask;

import com.qzw.demo.java.filehide.FileHideException;
import com.qzw.demo.java.filehide.FileHideV6;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * v2 支持选择文件夹
 * v3 自我修改二进制数据, 失败则报错
 * v4 保存v3的一些例子
 * v5 移动到filemask中
 *
 * @author BG388892
 * @date 2020/1/3
 */
@Log4j2
public class FileHideGUIV5 {
    Logger loggerFactory = LoggerFactory.getLogger("");
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    // 修改为jFrame就成了swing的了, 就能使用jButton了
    static Frame f = null;
    static FileDialog fileDialog = null;
    static TextArea ta;
    static Dialog dialog;
    static Button dialogOkBtn;
    static Label label;
    static MenuBar menuBar;
    static Menu menu;
    static MenuItem menuItem4Open = null;
    static MenuItem menuItem4Exit = null;

    static Button btn4NameEncrypt = new Button("文件名称加密");
    static Button btn4NameDecrypt = new Button("文件名称解密");

    static Button btn4ContentEncrypt = new Button("文件内容加密");
    static Button btn4ContentDecrypt = new Button("文件内容解密");


    static JButton jButton;

    public static void main(String[] args) {
        f = new Frame("FileMask");
        f.setLayout(new BorderLayout(0, 30));

//        Panel panel = new Panel(new FlowLayout());
//        panel.add(new Button("你好是饥饿"));
//
//        panel.add(new Button("你好是饥饿"));
//        panel.add(new Button("你好是饥饿"));
//        panel.add(new Button("你好是饥饿"));
//        panel.setBounds(0,0,600,500);
//        panel.setForeground(new Color(255,23,140));
//        panel.setBackground(new Color(	221,160,221));
//        panel.setName("你好是饥饿");
//        panel.setLocation(0,0);
//        panel.setSize(600,100);
//        panel.setVisible(true);
        // size bounds 不管用 foreground和backgroud是可以的

        f.setSize(600, 450);
        f.setLocation(300, 50);
        f.setIconImage(Toolkit.getDefaultToolkit().createImage("qq.png"));
        f.setVisible(true);
        f.addWindowListener(new MyWinAdapter());

        f.setResizable(false);
        menuBar = new MenuBar();
        menu = new Menu("file");
        menuItem4Open = new MenuItem("open");
        menuItem4Exit = new MenuItem("exit");

        menuBar.add(menu);
        menu.add(menuItem4Open);
        menu.add(menuItem4Exit);

        f.setMenuBar(menuBar);

        Panel panelNorthOuter = new Panel(new BorderLayout());
        panelNorthOuter.setBackground(new Color(190, 190, 190));
        panelNorthOuter.setPreferredSize(new Dimension(0, 70));

        Panel subPanel1 = new Panel(new FlowLayout(FlowLayout.LEFT));
        subPanel1.add(new Label("加密方式一:文件名称加密(对目标文件夹下所有的文件和文件夹的名称进行加密)"));
        Panel subPanel2 = new Panel(new FlowLayout(FlowLayout.RIGHT));
        subPanel2.add(btn4NameEncrypt);
        subPanel2.add(btn4NameDecrypt);


        panelNorthOuter.add(subPanel1, BorderLayout.NORTH);
        panelNorthOuter.add(subPanel2, BorderLayout.CENTER);

        Panel panelCenter = new Panel(new BorderLayout());
        panelCenter.setPreferredSize(new Dimension(0, 70));
        panelCenter.setBackground(new Color(199, 199, 199));
        Panel subPanel3 = new Panel(new FlowLayout(FlowLayout.LEFT));
        subPanel3.add(new Label("加密方式二:文件内容加密(对目标文件夹下所有的文件和文件夹的名称进行加密)"));
        Panel subPanel4 = new Panel(new FlowLayout(FlowLayout.RIGHT));
        subPanel4.add(btn4ContentEncrypt);
        subPanel4.add(btn4ContentDecrypt);

        panelCenter.add(subPanel3, BorderLayout.NORTH);
        panelCenter.add(subPanel4, BorderLayout.CENTER);

        // 北
        f.add(panelNorthOuter, BorderLayout.NORTH);
        // 中
        f.add(panelCenter, BorderLayout.CENTER);


        ta = new TextArea();
        ta.setPreferredSize(new Dimension(0, 200));
        // 南
        f.add(ta, BorderLayout.SOUTH);


        // 阻塞其他事件
        dialog = new Dialog(f, "提示", true);
        dialog.setLayout(new FlowLayout());
        dialogOkBtn = new Button("OK");
        label = new Label();
        dialog.add(label);
        dialog.add(dialogOkBtn);
        dialog.setLocation(400, 100);
        dialog.setSize(200, 200);
        eventResolver();

        encryptNameEventResolver();
        encryptContentEventResolver();


    }

    private static void encryptContentEventResolver() {
        btn4ContentDecrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ta.append("加密方式二:文件内容加密(对目标文件夹下所有的文件和文件夹的名称进行加密)\n\n");
            }
        });

        btn4ContentEncrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileSystemView fsv = FileSystemView.getFileSystemView();
                //C:\Users\BG388892\Desktop\新建文件夹 (4), 获取应用程序运行时所在的文件夹, 可以借此新增配置文件
                ta.append(System.getProperty("user.dir"));
                try {
                    testexe();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    log.info("加密出错", ex);
                }

            }
        });
    }

    public static void testexe() throws IOException {
//        File file = new File("C:\\Users\\BG388892\\Desktop\\filehideV100.exe");
        File file = new File(System.getProperty("user.dir") + "\\filehideV100.exe");
//        FileOutputStream fileOutputStream = new FileOutputStream(file,true);

        log.info(file.getPath());
        if (file.exists()) {
            System.out.println("文件存在");
            log.info("文件存在");
        } else {
            System.out.println("文件不存在");
            log.info("文件不存在");

            return;
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        System.out.println(raf.length());
        int read = raf.read();
        System.out.println(read);
        raf.seek(78);
        ta.append(String.valueOf(raf.read()));
        ta.append(String.valueOf(raf.read()));
        ta.append(String.valueOf(raf.read()));
        ta.append(String.valueOf(raf.read()));
        raf.seek(78);
        String str = "AAAAA";
        raf.write(str.getBytes());

        raf.close();

        System.out.println();
        System.out.println(str.getBytes().length);
    }

    private static void encryptNameEventResolver() {
        btn4NameEncrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String targetDir = directoryChoose();
                    if (targetDir == null) {
                        // 用户点击取消按钮, 取消加密
                        return;
                    }
                    new FileHideV6().encrypt(targetDir);
                } catch (FileHideException ex) {
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("解密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }


                label.setText("file name encrypt success !");
                dialog.setVisible(true);
            }
        });

        btn4NameDecrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String targetDir = directoryChoose();
                    if (targetDir == null) {
                        // 用户点击取消按钮, 取消加密
                        return;
                    }
                    new FileHideV6().decrypt(directoryAndFileChoose());
                } catch (FileHideException ex) {
                    if (ex.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    }
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                } catch (Exception ex) {
                    log.info("解密异常", ex);
                    label.setText(ex.getMessage());
                    dialog.setVisible(true);
                    return;
                }

                label.setText("file name decrypt success !");
                dialog.setVisible(true);
            }
        });


    }

    private static void eventResolver() {
        //事件
        menuItem4Exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        menuItem4Open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //选择文件夹
                JFileChooser fileChooser = new JFileChooser();
                FileSystemView fsv = FileSystemView.getFileSystemView();
                System.out.println(fsv.getHomeDirectory());
                fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
                fileChooser.setDialogTitle("请选择要上传的文件...");
                fileChooser.setApproveButtonText("确定");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int result = fileChooser.showOpenDialog(f);
                if (JFileChooser.APPROVE_OPTION == result) {
                    String path = fileChooser.getSelectedFile().getPath();
                    System.out.println("path: " + path);
                }
            }
        });

        dialogOkBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
    }

    public static class MyWinAdapter extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }
    }

    public static String directoryChoose() {
        //选择文件夹
        JFileChooser fileChooser = new JFileChooser();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        System.out.println(fsv.getHomeDirectory());
//        fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
//        fsv.getDefaultDirectory();
        File testDir = new File(fsv.getDefaultDirectory().getPath() + File.separatorChar + "fileMask");
        if (!testDir.exists()) {
            testDir.mkdir();
            // todo 隐藏
        }
        fileChooser.setCurrentDirectory(testDir);
        fileChooser.setDialogTitle("请选择要加密文件名的文件夹...");
        fileChooser.setApproveButtonText("确定");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(f);
        if (JFileChooser.APPROVE_OPTION == result) {
            String path = fileChooser.getSelectedFile().getPath();
            System.out.println("path: " + path);
            return path;
        } else {
            return null;
        }
    }

    public static String directoryAndFileChoose() {
        //选择文件夹
        JFileChooser fileChooser = new JFileChooser();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        System.out.println(fsv.getHomeDirectory());
//        fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
//        fsv.getDefaultDirectory();
        File testDir = new File(fsv.getDefaultDirectory().getPath() + File.separatorChar + "fileMask");
        if (!testDir.exists()) {
            testDir.mkdir();
            // todo 隐藏
        }
        fileChooser.setCurrentDirectory(testDir);
        fileChooser.setDialogTitle("请选择要加密文件名的文件夹...");
        fileChooser.setApproveButtonText("确定");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = fileChooser.showOpenDialog(f);
        if (JFileChooser.APPROVE_OPTION == result) {
            String path = fileChooser.getSelectedFile().getPath();
            System.out.println("path: " + path);
            return path;
        } else {
            return null;
        }
    }
}
