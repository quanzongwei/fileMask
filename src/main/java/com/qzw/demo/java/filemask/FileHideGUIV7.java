package com.qzw.demo.java.filemask;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBluer;
import com.qzw.demo.java.filemask.util.AuthenticationUtils;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.StringContent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

/**
 * v2 支持选择文件夹
 * v3 自我修改二进制数据, 失败则报错
 * v4 保存v3的一些例子
 * v5 移动到filemask中
 * v6 pannel移动到一个PanelFactory类中,并给出button click的监听事件
 * v7 新增使用帮助文档和联系作者
 * v8 支持中断功能
 *
 * @author BG388892
 * @date 2020/1/3
 */
@Log4j2
public class FileHideGUIV7 {
    Logger loggerFactory = LoggerFactory.getLogger("");
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    // 修改为jFrame就成了swing的了, 就能使用jButton了
    public static JFrame f = null;
    static FileDialog fileDialog = null;
    public static JTextArea ta;
    public static JDialog dialog;
    static JButton dialogOkBtn;
    public static JLabel label;
    static JMenuBar menuBar;
    static JMenu menu;
    static JMenu menu2;
    static JMenu menu3;
    static JMenuItem menuItem4Open = null;
    static JMenuItem menuItem4Exit = null;
    static JMenuItem menuItem4Help = null;
    static JMenuItem menuItem4Contact = null;

    static JDialog cancelDialog;

    static JButton btn4NameEncrypt = new JButton("文件名称加密");
    static JButton btn4NameDecrypt = new JButton("文件名称解密");

    static JButton btn4ContentEncrypt = new JButton("文件内容加密");
    static JButton btn4ContentDecrypt = new JButton("文件内容解密");


    static JButton jButton;

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

        PlasticLookAndFeel.setPlasticTheme(new DesertBluer());//设置主题
        try {
            //设置观感
//            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
//            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
//            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
//            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            log.error("UI样式设置出错", e);
        }
        cancelDialog = new JDialog(f, "提示");
        f = new JFrame("FileMask");
        SwingUtilities.updateComponentTreeUI(f);

        f.setLayout(new BorderLayout(0, 15));
//        f.setBackground(Color.gray);

        // 位置
        f.setSize(650, 640);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - f.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - f.getSize().height) / 2;
        f.setLocation(x, y);


        f.setIconImage(Toolkit.getDefaultToolkit().createImage("qq.png"));
        // 这里提前可见, 出现了渲染时差问题, 导致部分组件没有渲染出来
        // f.setVisible(true);
        f.addWindowListener(new MyWinAdapter());

        f.setResizable(true);
        menuBar = new JMenuBar();

        menu = new JMenu("文件");
        menu2 = new JMenu("使用帮助");
//        menu3 = new JMenu("联系作者");
        menuItem4Open = new JMenuItem("open");
        menuItem4Exit = new JMenuItem("退出");
        menuItem4Help = new JMenuItem("使用帮助");
        menuItem4Contact = new JMenuItem("联系作者");

        menu2.add(menuItem4Help);
        menu2.add(menuItem4Contact);

        menuBar.add(menu);
        menuBar.add(menu2);
//        menuBar.add(menu3);
//        menu.add(menuItem4Open);
        menu.add(menuItem4Exit);

        f.setJMenuBar(menuBar);
        f.setBackground(Color.black);
        // new
        JButton btn11 = new JButton("文件夹级联加密");
        JButton btn12 = new JButton("文件夹加密");
        JButton btn13 = new JButton("文件加密");
        JPanel panel1 = PanelFactory.generatePanelItem(btn11, btn12, btn13, "加密类型一:文件名称加密(支持对文件夹的名称加密)");


        JButton btn21 = new JButton("文件夹级联加密");
        JButton btn22 = new JButton("文件夹加密");
        JButton btn23 = new JButton("文件加密");
        JPanel panel2 = PanelFactory.generatePanelItem(btn21, btn22, btn23, "加密类型二:文件头部加密");

        JButton btn31 = new JButton("文件夹级联加密");
        JButton btn32 = new JButton("文件夹加密");
        JButton btn33 = new JButton("文件加密");
        JPanel panel3 = PanelFactory.generatePanelItem(btn31, btn32, btn33, "加密类型三:文件内容加密(加密速度较慢, 1G大小的文件耗时约10秒)");


        JButton btn41 = new JButton("文件夹级联解密");
        JButton btn42 = new JButton("文件夹解密");
        JButton btn43 = new JButton("文件解密");
        JPanel panel4 = PanelFactory.generatePanelItem(btn41, btn42, btn43, "文件解密(系统自动识别文件的加密类型, 并执行解密操作)");


        JPanel panelCombine1 = new JPanel(new BorderLayout(0, 20));
        panelCombine1.setMaximumSize(new Dimension(650, 100));
        panelCombine1.setMinimumSize(new Dimension(650, 100));

        panelCombine1.add(panel1, BorderLayout.NORTH);
        panelCombine1.add(panel2, BorderLayout.CENTER);

        JPanel panelCombine2 = new JPanel(new BorderLayout(0, 20));
        panelCombine1.setMaximumSize(new Dimension(650, 100));
        panelCombine1.setMinimumSize(new Dimension(650, 100));
        panelCombine2.add(panel3, BorderLayout.NORTH);
        panelCombine2.add(panel4, BorderLayout.CENTER);
////        Border bevelBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
//        Border bevelBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED),"加密方式一:文件名称加密(对目标文件夹下所有的文件和文件夹的名称进行加密)");
//        panelCombine1.setBorder(bevelBorder);
//        Border raiseBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
//        panelCombine2.setBorder(raiseBorder);

        // 北
        f.add(panelCombine1, BorderLayout.NORTH);
        // 中
        f.add(panelCombine2, BorderLayout.CENTER);

        JPanel panelCombine3 = new JPanel(new BorderLayout(0, 20));
        panelCombine3.add(panelCombine1, BorderLayout.NORTH);
        panelCombine3.add(panelCombine2, BorderLayout.CENTER);
        panelCombine1.setMaximumSize(new Dimension(650, 220));
        panelCombine1.setMinimumSize(new Dimension(650, 220));


        ta = new JTextArea();
        ta.setAutoscrolls(true);
        ta.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),"输出日志:"));
        ta.setForeground(new Color(56,131,56));

//        ta.setBackground(Color.darkGray);
//        ta.setForeground(Color.black);
//        ta.setBackground(new Color(162,162,162));
//        ta.setBackground(Color.lightGray);
        // 南
        f.add(panelCombine3, BorderLayout.NORTH);
        //中
        JScrollPane scrollPane = new JScrollPane(ta);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        f.add(scrollPane, BorderLayout.CENTER);

        // 阻塞其他事件
        dialog = new JDialog(f, "提示", true);
        dialog.setLayout(new BorderLayout());
        dialogOkBtn = new JButton("OK");
        label = new JLabel();
//        label.setAlignment(Label.CENTER);
        
        dialog.add(label, BorderLayout.NORTH);

        JPanel dialogPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dialogPanel.add(dialogOkBtn);


        dialog.add(dialogPanel, BorderLayout.CENTER);
        dialog.setSize(200, 100);
        // 对话框位置
        int x1 = (f.getSize().width - dialog.getSize().width) / 2 + (int) f.getLocation().getX();
        int y1 = (f.getSize().height - dialog.getSize().height) / 2 + (int) f.getLocation().getY();
        dialog.setLocation(x1, y1);
        //
        eventResolver();
        // btn事件绑定

        ButtonActionFactory.btn11(btn11);
        ButtonActionFactory.btn12(btn12);
        ButtonActionFactory.btn13(btn13);

        ButtonActionFactory.btn21(btn21);
        ButtonActionFactory.btn22(btn22);
        ButtonActionFactory.btn23(btn23);

        ButtonActionFactory.btn31(btn31);
        ButtonActionFactory.btn32(btn32);
        ButtonActionFactory.btn33(btn33);

        ButtonActionFactory.btn41(btn41);
        ButtonActionFactory.btn42(btn42);
        ButtonActionFactory.btn43(btn43);


        f.setVisible(true);

        // 用户认证
        // 第一次使用
        if (!AuthenticationUtils.isExistUserPassword()) {
            String pass = null;
            pass = passDialog(f);
            AuthenticationUtils.setUserMd5Byte(pass);
            return;
        }
        //校验是否是当前用户
        check4SecondTime(f);

        // 如果用户已经设置设置完密码了, 同时也是当前合法用户, 那么,程序正常运行
        return;

    }

    private static void check4SecondTime(JFrame f) {
        String pass2 = passDialog4SecondTimes(f);
        if (pass2 == null || pass2.equals("")) {
            nullCHeck(f, 1);
        } else {

            if (!AuthenticationUtils.isCurrentUser(pass2)) {
                JOptionPane.showConfirmDialog(f, "对不起密码错误,请重新输入", "提示", JOptionPane.DEFAULT_OPTION);
                check4SecondTime(f);
            }
        }
    }

    private static String passDialog(JFrame f) {
        //todo qzw 输入校验
        String pass1 = JOptionPane.showInputDialog(f, "您第一次使用该软件, 请设置密码:");
        if (pass1 == null || pass1.equals("")) {
            return nullCHeck(f, 0);
        } else {
            if (!isValidPassword(pass1)) {
                JOptionPane.showConfirmDialog(f, "密码不合法,请重新输入!(只允许包含数字和字母,位数是1-20位)", "提示", JOptionPane.DEFAULT_OPTION);
                return passDialog4SecondTimes(f);
            }

            String pass2 = JOptionPane.showInputDialog(f, "请再次确认密码(忘记密码会导致文件无法解密)!");
            if (pass2 == null || pass2.equals("")) {
                return nullCHeck(f, 0);
            } else {
                if (!pass1.equals(pass2)) {
                    JOptionPane.showConfirmDialog(f, "两次密码不相同, 请重新输入!", "提示", JOptionPane.DEFAULT_OPTION);
                    return passDialog(f);
                }
            }
        }
        return pass1;
    }

    private static String passDialog4SecondTimes(JFrame f) {
        String pass1 = JOptionPane.showInputDialog(f, "请输入密码:");
        if (pass1 == null || pass1.equals("")) {

            return nullCHeck(f, 1);
        }
        if (!isValidPassword(pass1)) {
            JOptionPane.showConfirmDialog(f, "密码不合法,请重新输入!(只允许包含数字和字母,位数是1-20位)", "提示", JOptionPane.DEFAULT_OPTION);
            return passDialog4SecondTimes(f);
        }

        return pass1;
    }

    public static boolean isValidPassword(String pass) {
        if (pass.matches("[A-Za-z0-9]{1,20}")) {
            return true;
        }
        return false;
    }

    private static String nullCHeck(JFrame f, int opType) {
        int value = JOptionPane.showConfirmDialog(f, "您未输入任何有效的数据,软件即将退出!(点击取消可以返回重新输入)", "提示", JOptionPane.OK_CANCEL_OPTION);
        if (value == JOptionPane.CANCEL_OPTION) {
            // 初始化
            if (opType == 0) {
                return passDialog(f);
            } else if (opType == 1) {
                return passDialog4SecondTimes(f);
            }
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        System.exit(0);
        return null;
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


    private static void setLabel(JLabel label) {
        label.setMaximumSize(new Dimension(200,30));
        label.setMinimumSize(new Dimension(200,30));
    }


    private static void eventResolver() {
        //事件
        menuItem4Exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        //事件
        menuItem4Help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = new JFrame("使用说明文档");
                jFrame.setMaximumSize(new Dimension(300,300));
                jFrame.setMinimumSize(new Dimension(300,300));

                JPanel jpanel = new JPanel(new FlowLayout());
                jpanel.setMaximumSize(new Dimension(300, 300));
                jpanel.setMinimumSize(new Dimension(300, 300));
                JLabel label1 = new JLabel("                                      ");
                JLabel label2 = new JLabel("使用说明: https://blog.csdn.net/starcrm/article/details/52576423");

                JLabel label3 = new JLabel("                              ");
                JLabel label4 = new JLabel("原理说明: https://blog.csdn.net/starcrm/article/details/52576423");
                JLabel label5 = new JLabel("                             ");
                JLabel label6 = new JLabel("花絮: https://blog.csdn.net/starcrm/article/details/52576423");
                JLabel label7 = new JLabel("                           ");
                JLabel label8 = new JLabel("常见问题: https://blog.csdn.net/starcrm/article/details/52576423");
                JLabel label9 = new JLabel("                              ");
                setLabel(label1);
                setLabel(label2);
                setLabel(label3);
                setLabel(label4);
                setLabel(label5);
                setLabel(label6);
                setLabel(label7);
                setLabel(label8);
                setLabel(label9);
                jpanel.add(label1);
                jpanel.add(label2);
                jpanel.add(label3);
                jpanel.add(label4);
                jpanel.add(label5);
                jpanel.add(label6);
                jpanel.add(label7);
                jpanel.add(label8);
                jpanel.add(label9);

                //
                JTextPane jTextPane = new JTextPane();
                JPanel jPanel = new JPanel();
                jpanel.add(jTextPane);

                jTextPane.setAutoscrolls(true);

                Container container = jFrame.getContentPane();
                JTextArea jTextArea = new JTextArea();
                JScrollPane scrollPane = new JScrollPane(jTextPane);
//                scrollPane.setMaximumSize(new Dimension(650,700));
//                scrollPane.setMaximumSize(new Dimension(650,700));
                StringBuilder text = new StringBuilder("");

                File file = new File(System.getProperty("user.dir") + File.separatorChar + "doc" + File.separatorChar
                        + "readme.html");

                scrollPane.add(jpanel);

//                Highlighter highLighter = new DefaultHighlighter();
//                jTextPane.setHighlighter(highLighter);
                StringContent sc = new StringContent(15000);
                try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"))) {
                    String s = "";
                    while ((s = bf.readLine())!= null) {
                        text.append(s);
                        text.append("\n");
                        jTextArea.append(s);
                        jTextArea.append("\n");

                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                jTextArea.append("<HTML>Click the <FONT color=\\\"#000099\\\"><U>link</U></FONT>\"\n" +
                        "        + \" to go to the Java website.</HTML>");
//                try {
////                    sc.insertString(0, text.toString());
//                } catch (BadLocationException ex) {
//                    ex.printStackTrace();
//                }


//                container.add(scrollPane);
                jTextPane.setContentType("text/html");
                jTextPane.setText(text.toString());

                container.add(scrollPane);



                jFrame.setSize(800, 800);
                int x = (Toolkit.getDefaultToolkit().getScreenSize().width - jFrame.getSize().width) / 2;
                int y = (Toolkit.getDefaultToolkit().getScreenSize().height - jFrame.getSize().height) / 2;
                jFrame.setLocation(x, y>f.getLocation().getY()? (int) f.getLocation().getY() :y);

                //
                jFrame.setVisible(true);

            }
        });

        menuItem4Contact.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = new JFrame("有任何问题,欢迎联系作者~~");
                jFrame.setSize(new Dimension(400,200));

                Container container = jFrame.getContentPane();
                container.setLayout(new BorderLayout(0,0));
                container.setSize(new Dimension(400,400));

                JLabel jLabel = new JLabel("作者邮箱: 552114141@qq.com");
                jLabel.setSize(new Dimension(400,30));

//                JLabel jLabel2 = new JLabel("作者微信: quanzongwei");
                JLabel jLabel2 = new JLabel("Hello <font color=\\\"#00ff00\\\">World</font>");
                jLabel.setSize(new Dimension(400,30));

//                container.add(new Label());
//
//                container.add(new Label());
//                container.add(jLabel);
//                container.add(new Label());
//                container.add(jLabel2);
//                container.add(new Label());
//                container.add(new Label());

                JTextPane jTextPane =new JTextPane();
                jTextPane.setContentType("text/html");

                jTextPane.setText("<html>\n" +
                        "<p>\n" +
                        "<h2 align=\"center\">作者邮箱: <font color=\"red\">552114141@qq.com</font></h2>\n" +
                        "<h2 align=\"center\">作者微信: <font color=\"red\">quanzongwei</font></h2>\n" +
                        "</p>\n" +
                        "</html>");

                container.add(jTextPane);



                int x = (Toolkit.getDefaultToolkit().getScreenSize().width - jFrame.getSize().width) / 2;
                int y = (Toolkit.getDefaultToolkit().getScreenSize().height - jFrame.getSize().height) / 2;
                jFrame.setLocation(x, y);
                //
                jFrame.setVisible(true);
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
}
