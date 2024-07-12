package com.qzw.filemask.service;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertBluer;
import com.qzw.filemask.component.PlatformContext;
import com.qzw.filemask.constant.Constants;
import com.qzw.filemask.enums.PlatformEnum;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * 平台定制逻辑收敛
 *
 * @author quanzognwei
 * @date 2024/7/12 21:34
 */
@Log4j2
public class PlatformService {

    /**
     * 返回用户认证目录
     */
    public static String getAuthDirNameByPlatform(String platform) {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        if (PlatformEnum.WINDOWS.name().equals(platform)) {
            //File tmpDir = new File(fsv.getDefaultDirectory().getPath() + File.separatorChar + "fileMask");
            return System.getProperty("user.dir") + Constants.FILE_MASK_AUTHENTICATION_NAME;
        } else if (PlatformEnum.MAC.name().equals(platform)) {
            return fsv.getDefaultDirectory().getPath() + File.separatorChar + Constants.FILE_MASK_AUTHENTICATION_NAME;
        }
        throw new RuntimeException("平台不支持:" + platform);
    }

    /*
        设置样式
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        PlasticLookAndFeel.setPlasticTheme(new DesertBluer());
        PlasticLookAndFeel.setPlasticTheme(new SkyYellow());
        设置观感
        UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
        UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
        UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
        UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
        UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
     */
    public static void setLookAndFeelByPlatform(String platform) {
        try {
            if (PlatformEnum.WINDOWS.name().equals(platform)) {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                PlasticLookAndFeel.setPlasticTheme(new DesertBluer());
                //设置观感
                UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
                return;
            } else if (PlatformEnum.MAC.name().equals(platform)) {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                PlasticLookAndFeel.setPlasticTheme(new DesertBluer());
                return;
            }
            throw new RuntimeException("平台不支持:" + platform);
        } catch (Exception e) {
            log.error("UI样式设置出错", e);
        }
    }
}
