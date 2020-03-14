package com.qzw.demo.java.filehide;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

/**
 * @author BG388892
 * @date 2020/1/3
 */
public class FileHideGUI {
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    static Frame f = null;
    static FileDialog fileDialog = null;
    static TextArea ta;
    static Dialog dialog;
    static Button dialogOkBtn;
    static Label label;
    static MenuItem menuItem = null;
    static MenuItem menuItem2 = null;


    public static void main(String[] args) {
        f = new Frame("FileMask");
        f.setSize(400, 600);
        f.setLocation(300, 50);
        f.setIconImage(Toolkit.getDefaultToolkit().createImage("qq.png"));
        f.setVisible(true);
        f.addWindowListener(new MyWinAdapter());

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("file");
        menuItem = new MenuItem("open");
        menuItem2 = new MenuItem("exit");

        menuBar.add(menu);
        menu.add(menuItem);
        menu.add(menuItem2);

        f.setMenuBar(menuBar);

        ta = new TextArea();
        f.add(ta);

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


    }

    private static void eventResolver() {
        //事件
        menuItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileDialog = new FileDialog(f, "打开文件", FileDialog.LOAD);
                fileDialog.setVisible(true);

                // D:\ad\
                String dirName = fileDialog.getDirectory();
                // admin.txt
                String fileName = fileDialog.getFile();
                System.out.println(dirName);
                System.out.println(fileName);
                ta.setBounds(100, 150, 200, 300);
                ta.append(dirName + LINE_SEPARATOR + fileName);

                //
                if (Objects.equals(fileName, "admin.txt")) {
                    label.setText("文件不合法,admin.txt\n");
                    dialog.setVisible(true);
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
