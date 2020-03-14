package com.qzw.demo.java.filemask;

import javax.swing.*;
import java.awt.*;

/**
 * @author BG388892
 * @date 2020/2/24
 */
public class PanelFactory {


    public static JPanel generatePanelItem(JButton btn1, JButton btn2, JButton btn3, String label) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
//        panel.setBackground(new Color(190, 190, 190));
        panel.setMinimumSize(new Dimension(640, 40));
        panel.setMaximumSize(new Dimension(640, 40));
        JPanel subPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        subPanel1.add(new Label(label));
        JPanel subPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (btn1 != null) {
//            btn1.setBackground(Color.lightGray);
//            btn1.setBackground(new Color(162,162,162));
            subPanel2.add(btn1);
        }
        if (btn2 != null) {
//            btn2.setBackground(Color.lightGray);
//            btn2.setBackground(new Color(162,162,162));
            subPanel2.add(btn2);
        }
        subPanel2.add(btn3);
        panel.add(subPanel1,BorderLayout.NORTH);
        panel.add(subPanel2,BorderLayout.CENTER);
//        btn3.setBackground(Color.lightGray);
//        btn3.setBackground(new Color(162,162,162));

        // 5是实线
        float[] dash1 = {2.0f};
        BasicStroke s = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);


//        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),label));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createStrokeBorder(s), label));
//        panel.setBounds(2,2,650-2-2,40-2-2);


        return panel;
    }
}
