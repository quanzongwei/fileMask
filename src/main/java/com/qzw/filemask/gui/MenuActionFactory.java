package com.qzw.filemask.gui;

import com.qzw.filemask.FileMaskMain;
import com.qzw.filemask.constant.Constants;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * @author quanzongwei
 * @date 2020/3/14
 */
@Log4j2
public class MenuActionFactory {

    public static JFrame f = FileMaskMain.f;

    public static void menuItem4Exit(JMenuItem item) {
        item.addActionListener(e -> System.exit(0));
    }

    public static void menuItem4Help(JMenuItem item) {
        item.addActionListener(e -> {
            JFrame jFrame = new JFrame("使用说明文档");
            jFrame.setMaximumSize(new Dimension(300, 300));
            jFrame.setMinimumSize(new Dimension(300, 300));

            JPanel jpanel = new JPanel(new FlowLayout());
            jpanel.setMaximumSize(new Dimension(300, 300));
            jpanel.setMinimumSize(new Dimension(300, 300));

            //
            JTextPane jTextPane = new JTextPane();
            jTextPane.setAutoscrolls(true);
            jpanel.add(jTextPane);


            Container container = jFrame.getContentPane();
            JScrollPane scrollPane = new JScrollPane(jTextPane);
            StringBuilder text = new StringBuilder("");
            File file = new File(Constants.DOC_PATH);
            scrollPane.add(jpanel);
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
                String s = "";
                while ((s = bf.readLine()) != null) {
                    text.append(s);
                    text.append("\n");

                }
            } catch (FileNotFoundException ex) {
                log.error("doc not found !!", ex);
            } catch (IOException ex) {
                log.error("file open error !!", ex);
            }

            jTextPane.setContentType("text/html");
            jTextPane.setText(text.toString());
            container.add(scrollPane);

            jFrame.setSize(800, 800);
            int x = (Toolkit.getDefaultToolkit().getScreenSize().width - jFrame.getSize().width) / 2;
            int y = (Toolkit.getDefaultToolkit().getScreenSize().height - jFrame.getSize().height) / 2;
            jFrame.setLocation(x, y > f.getLocation().getY() ? (int) f.getLocation().getY() : y);
            //
            jFrame.setVisible(true);


        });
    }

    public static void menuItem4Contact(JMenuItem item) {
        item.addActionListener(e -> {
            JFrame jFrame = new JFrame("有任何问题,欢迎联系作者~~");
            jFrame.setSize(new Dimension(400, 200));

            Container container = jFrame.getContentPane();
            container.setLayout(new BorderLayout(0, 0));
            container.setSize(new Dimension(550, 400));
            container.setMaximumSize(new Dimension(550, 400));
            container.setMinimumSize(new Dimension(550, 400));

            JTextPane jTextPane = new JTextPane();
            jTextPane.setContentType("text/html");

            jTextPane.setText("<html>\n" +
                    "" +
                    "<h2 align=\"center\">作者邮箱: <font color=\"red\">552114141@qq.com</font></h2>\n" +
                    "<h2 align=\"center\">作者微信: <font color=\"red\">quanzongwei</font></h2>\n" +
                    "<h4 align=\"center\">github repository: <font color=\"red\">https://github.com/quanzongwei/fileMask</font></h4>\n" +
                    "<h5 align=\"right\">版本号: <font color=\"red\">1.1 </font></h5>\n" +
                    "</p>" +
                    "</html>");

            container.add(jTextPane);

            int x = (Toolkit.getDefaultToolkit().getScreenSize().width - jFrame.getSize().width) / 2;
            int y = (Toolkit.getDefaultToolkit().getScreenSize().height - jFrame.getSize().height) / 2;
            jFrame.setLocation(x, y);
            //
            jFrame.setVisible(true);
        });
    }
}
