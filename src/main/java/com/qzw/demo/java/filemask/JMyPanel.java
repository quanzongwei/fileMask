package com.qzw.demo.java.filemask;

import org.apache.logging.log4j.core.Layout;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author BG388892
 * @date 2020/3/7
 */
public class JMyPanel extends JPanel {

    public JMyPanel(LayoutManager layout) {
        super(layout);
    }

    /**
     * 重写paint方法
     */
    @Override
    public void paint(Graphics g){

        super.paint(g);
        Color mfColor = Color.GRAY;
        Rectangle2D mfRect = new Rectangle2D.Float();
        float[] dash1 = {5.0f};
        BasicStroke s = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

        Graphics2D g2d = (Graphics2D)g;
        //设置边框颜色
        g2d.setColor(mfColor);
        //设置边框范围
        mfRect.setRect(0,0,getWidth()-10,getHeight()-2);
        //设置边框类型
        g2d.setStroke(s);

        g2d.draw(mfRect);

    }

}
