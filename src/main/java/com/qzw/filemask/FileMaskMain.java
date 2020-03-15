package com.qzw.filemask;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBluer;
import com.qzw.filemask.service.LoginService;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * fileMask main class
 * @author quanzongwei
 * @date 2020/1/3
 */
@Log4j2
public class FileMaskMain {
    public static JFrame f = null;
    public static JTextArea ta;
    public static JDialog dialog;
    static JButton dialogOkBtn;
    public static JLabel label;
    static JMenuBar menuBar;
    static JMenu menu;
    static JMenu menu2;
    static JMenuItem menuItem4Exit = null;
    static JMenuItem menuItem4Help = null;
    static JMenuItem menuItem4Contact = null;

    static JDialog cancelDialog;


    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        //设置主题
        PlasticLookAndFeel.setPlasticTheme(new DesertBluer());
        try {
            //设置观感
            //UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
            UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
            //UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
            //UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (Exception e) {
            log.error("UI样式设置出错", e);
        }
        f = new JFrame("FileMask");
        f.setLayout(new BorderLayout(0, 15));
        // 位置
        f.setSize(650, 640);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - f.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - f.getSize().height) / 2;
        f.setLocation(x, y);
        f.setIconImage(Toolkit.getDefaultToolkit().createImage("qq.png"));
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setResizable(true);

        cancelDialog = new JDialog(f, "提示");

        menuBar = new JMenuBar();
        menu = new JMenu("文件");
        menu2 = new JMenu("使用帮助");
        menuItem4Exit = new JMenuItem("退出");
        menuItem4Help = new JMenuItem("使用帮助");
        menuItem4Contact = new JMenuItem("联系作者");

        menu.add(menuItem4Exit);
        menu2.add(menuItem4Help);
        menu2.add(menuItem4Contact);

        menuBar.add(menu);
        menuBar.add(menu2);

        f.setJMenuBar(menuBar);
        f.setBackground(Color.black);
        // button and panel
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

        JPanel panelCombine3 = new JPanel(new BorderLayout(0, 20));
        panelCombine3.add(panelCombine1, BorderLayout.NORTH);
        panelCombine3.add(panelCombine2, BorderLayout.CENTER);
        panelCombine1.setMaximumSize(new Dimension(650, 220));
        panelCombine1.setMinimumSize(new Dimension(650, 220));

        // north
        f.add(panelCombine3, BorderLayout.NORTH);

        //中
        ta = new JTextArea();
        ta.setAutoscrolls(true);
        ta.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), "输出日志:"));
        ta.setForeground(new Color(56, 131, 56));

        JScrollPane scrollPane = new JScrollPane(ta);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        f.add(scrollPane, BorderLayout.CENTER);

        dialog = new JDialog(f, "提示", true);
        dialog.setLayout(new BorderLayout());
        dialogOkBtn = new JButton("OK");
        dialogOkBtn.addActionListener(e -> dialog.setVisible(false));
        label = new JLabel();
        dialog.add(label, BorderLayout.NORTH);

        JPanel dialogPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dialogPanel.add(dialogOkBtn);

        dialog.add(dialogPanel, BorderLayout.CENTER);
        dialog.setSize(200, 100);

        // 对话框位置
        int x1 = (f.getSize().width - dialog.getSize().width) / 2 + (int) f.getLocation().getX();
        int y1 = (f.getSize().height - dialog.getSize().height) / 2 + (int) f.getLocation().getY();
        dialog.setLocation(x1, y1);
        // menuItem action bind
        MenuActionFactory.menuItem4Exit(menuItem4Exit);
        MenuActionFactory.menuItem4Help(menuItem4Help);
        MenuActionFactory.menuItem4Contact(menuItem4Contact);
        //btn事件绑定
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
        //登录
        LoginService.doLogin(f);
    }


}
