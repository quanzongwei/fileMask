package com.qzw.filemask.service;

import com.qzw.filemask.FileMaskMain;
import com.qzw.filemask.enums.ChooseTypeEnum;
import com.qzw.filemask.enums.FileEncoderTypeEnum;
import com.qzw.filemask.fileencoder.AbstractFileEncoder;
import com.qzw.filemask.fileencoder.FileContentEncoder;
import com.qzw.filemask.model.TailModel;
import com.qzw.filemask.service.status.ComputingStatusService;
import com.qzw.filemask.service.status.OperationLockStatusService;
import com.qzw.filemask.service.status.StopCommandStatusService;
import com.qzw.filemask.util.PasswordUtil;
import com.qzw.filemask.util.PrivateDataUtils;
import com.qzw.filemask.util.ThreadPoolUtil;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;

/**
 * 主界面工作流程服务(页面交互,异步刷新,统计展示等等逻辑)
 *
 * @author BG388892
 * @date 2020/6/1
 */
@Log4j2
public class WorkFlowService {
    private static JTextArea ta = FileMaskMain.ta;
    private static JFrame f = FileMaskMain.f;

    /**
     * 执行加密或者解密操作
     */
    public static void doEncryptOrDecrypt(String targetFileOrDir, AbstractFileEncoder fileEncoder, ChooseTypeEnum chooseTypeEnum, boolean isEncryptOperation) {
        String magicWord = getOperationType(isEncryptOperation);
        if (targetFileOrDir == null) {
            // 用户点击取消按钮, 取消加密
            return;
        }
        if (!isValidPath(targetFileOrDir)) {
            JOptionPane.showConfirmDialog(f, magicWord + "路径太短, 请重新选择则!", "提示", JOptionPane.DEFAULT_OPTION);
            return;
        }

        JPanel fileInfoPanel = new JPanel(new BorderLayout(0, 10));

        JLabel fileInfoDialogLabel = new JLabel("");
        fileInfoDialogLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fileInfoDialogLabel.setForeground(Color.RED);
        fileInfoDialogLabel.setBackground(Color.black);

        JTextArea fileInfoDialogTextArea = new JTextArea();
        fileInfoDialogTextArea.setBackground(Color.WHITE);

        fileInfoPanel.add(fileInfoDialogTextArea, BorderLayout.NORTH);
        fileInfoPanel.add(fileInfoDialogLabel, BorderLayout.CENTER);

        JButton stopCommandBtn = new JButton("提前停止");
        stopCommandBtn.addActionListener(e -> {
            StopCommandStatusService.setStopStatus(StopCommandStatusService.STOP_STATUS_REQUIRE_STOP);
            stopCommandBtn.setVisible(false);
        });
        fileInfoPanel.add(stopCommandBtn, BorderLayout.SOUTH);

        //文件信息统计对话框
        JDialog fileInfoDialog = new JDialog(f);
        fileInfoDialog.setTitle("FileMask运行状态");
        fileInfoDialog.setSize(f.getWidth() / 2, 400);
        fileInfoDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        fileInfoDialog.setModal(true);
        fileInfoDialog.add(fileInfoPanel);
        //文件信息统计对话框居中展示
        int x = (int) ((f.getSize().width - fileInfoDialog.getSize().width) / 2 + f.getLocation().getX());
        int y = (int) ((f.getSize().height - fileInfoDialog.getSize().height) / 2 + f.getLocation().getY());
        fileInfoDialog.setLocation(x, y);

        //刷新文本区域
        refreshTextArea(fileInfoDialogTextArea);
        //刷新文本标签
        refreshLabel(fileInfoDialogLabel, isEncryptOperation);

        //异步执行加密解密任务
        asyncRunActualTaskThread(targetFileOrDir, stopCommandBtn, fileEncoder, chooseTypeEnum, isEncryptOperation);
        //异步刷新文件统计信息对话框
        asyncRefreshDialog(fileInfoDialog, fileInfoDialogTextArea, isEncryptOperation, fileInfoDialogLabel);

        fileInfoDialog.setVisible(true);
    }


    private static void refreshLabel(JLabel label, boolean isEncryptOperation) {
        Integer stopStatus = StopCommandStatusService.getStopStatus();
        Integer computeStatus = ComputingStatusService.getComputeStatus();
        // 没有收到停止命令
        if (stopStatus.equals(StopCommandStatusService.STOP_STATUS_NOT_REQUIRE_STOP)) {
            if (computeStatus.equals(ComputingStatusService.COMPUTE_STATUS_RUNNING_COMPUTING)) {
                label.setText("正在统计文件信息(请勿关闭软件或计算机)");
            } else {
                label.setText("正在执行" + getOperationType(isEncryptOperation) + "操作(请勿关闭软件或计算机)");
            }
        } else {
            if (computeStatus.equals(ComputingStatusService.COMPUTE_STATUS_RUNNING_COMPUTING)) {
                label.setText("正在等待当前文件处理完成(请勿关闭软件或计算机)");
            } else {
                label.setText("正在等待当前文件处理完成(请勿关闭软件或计算机)");
            }
        }
    }

    private static void asyncRefreshDialog(JDialog fileInfoDialog, JTextArea runningDialogTextArea, boolean isEncryptOperation, JLabel runningDialogLabel) {
        String magicWord = getOperationType(isEncryptOperation);
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        executorService.submit(() -> {
            while (true) {
                refreshLabel(runningDialogLabel, isEncryptOperation);
                refreshTextArea(runningDialogTextArea);
                // 校验是否释放运行锁
                boolean lock = OperationLockStatusService.lock();
                if (lock == true) {
                    //关闭对话框
                    fileInfoDialog.dispose();
                    //生成新的摘要对话框
                    JOptionPane.showConfirmDialog(f, "成功" + magicWord + "文件: " + StatisticsService.getDoneFileTotalAmount() + "个\n一共扫描文件: " + StatisticsService.getScanFileTotalAmount() + "个\n本次操作耗时: " + StatisticsService.generateTotalSpendTimeInHuman(), magicWord + "成功!", JOptionPane.DEFAULT_OPTION);
                    //重置所有统计数据
                    StatisticsService.clearAll();
                    //重置提前停止命令为:无需提前停止
                    StopCommandStatusService.setStopStatus(StopCommandStatusService.STOP_STATUS_NOT_REQUIRE_STOP);
                    //重置计算状态为:正在计算统计文件信息
                    ComputingStatusService.setComputeStatus(ComputingStatusService.COMPUTE_STATUS_RUNNING_COMPUTING);
                    //释放锁
                    OperationLockStatusService.releaseLock();
                    return;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        });
    }

    private static void refreshTextArea(JTextArea dialog) {
        dialog.setText("");
        dialog.setEditable(false);
        dialog.append("（一）、已扫描文件统计数据\n");
        dialog.append("已扫描文件总个数: " + StatisticsService.scanFileTotalAmount + "\n");
        dialog.append("待处理文件总大小: " + StatisticsService.getTodoFileTotalBytesInHuman() + "\n");
        dialog.append("" + "\n");
        dialog.append("（二）、已扫描文件处理进度\n");
        dialog.append("已处理文件总数: " + StatisticsService.getDoneFileTotalAmount() + "\n");
        dialog.append("已处理时长: " + StatisticsService.generateTotalSpendTimeInHuman() + "\n");
        dialog.append("预计剩余时长: " + StatisticsService.generateLastTime() + "\n");
        dialog.append("" + "\n");
        dialog.append("（三）、当前文件处理进度\n");
        dialog.append("当前文件名称: " + StatisticsService.getCurrentFileName() + "\n");
        dialog.append("当前文件所在文件夹夹: " + StatisticsService.getCurrentFileParentPath() + "\n");
        dialog.append("当前文件大小: " + StatisticsService.getCurrentFileBytesInhuman() + "\n");
        dialog.append("当前文件已处理大小: " + StatisticsService.getCurrentFileEncryptBytesInHuman() + "\n");
        dialog.append("当前文件已处理时长: " + StatisticsService.getCurrentFileEncryptSpentTimeInHuman() + "\n");
        dialog.append("当前文件预计剩余时长: " + StatisticsService.computeCurrentFileLastTimeInHuman() + "\n");
    }

    private static void asyncRunActualTaskThread(String targetFileOrDir, JButton stopCommandBtn, AbstractFileEncoder fileEncoder, ChooseTypeEnum chooseTypeEnum, boolean isEncryptOperation) {
        String magicWord = getOperationType(isEncryptOperation);
        ExecutorService executorService = ThreadPoolUtil.getExecutorService();
        executorService.submit(() -> {
            // 计算文件信息
            try {
                //获取锁
                OperationLockStatusService.lock();
                //统计文件信息
                computeFileInfo(new File(targetFileOrDir), fileEncoder, chooseTypeEnum, isEncryptOperation);
                //没有收到停止命令,执行状态从计算中变为加密中
                if (!StopCommandStatusService.getStopStatus().equals(StopCommandStatusService.STOP_STATUS_REQUIRE_STOP)) {
                    ComputingStatusService.setComputeStatus(ComputingStatusService.COMPUTE_STATUS_RUNNING_ENCRYPT);
                }
                //收到停止命令,返回
                else {
                    return;
                }
                // [统计] 设置加密开始时间
                StatisticsService.setOperationBeginTime(System.currentTimeMillis());
                if (isEncryptOperation) {
                    fileEncoder.encodeFileOrDir(new File(targetFileOrDir), chooseTypeEnum);
                } else {
                    new AbstractFileEncoder() {
                        @Override
                        public FileEncoderTypeEnum getFileEncoderType() {
                            //解密用不到这个参数
                            return null;
                        }
                    }.decodeFileOrDir(new File(targetFileOrDir), chooseTypeEnum);
                }
            } catch (Exception ex) {
                log.info(magicWord + "操作异常" + ",文件路径:" + targetFileOrDir, ex);
                ta.append(magicWord + "操作异常:" + ex.getMessage() + "\r\n");
                JOptionPane.showConfirmDialog(f, magicWord + "操作出错!!!", "提示", JOptionPane.DEFAULT_OPTION);
                return;
            } finally {
                ta.append(magicWord + "操作成功, 耗时:" + StatisticsService.generateTotalSpendTimeMillisecondsInHuman() + ", 路径名: " + targetFileOrDir + "\r\n");
                //释放锁
                OperationLockStatusService.releaseLock();
                //[统计] 清除统计数据放在asyncRefreshDialog方法中执行
            }
        });
    }

    /**
     * 统计文件信息
     * 1. 文件名称加密,统计文件和文件夹总数
     * 2. 文件头部加密,统计文件总数
     * 3. 文件内容加密,统计文件总数和待加密的文件总数
     */
    private static void computeFileInfo(File file, AbstractFileEncoder fileEncoder, ChooseTypeEnum chooseTypeEnum, boolean isEncryptOperation) {
        //解密操作没有该参数
        if (fileEncoder == null) {
            //无任何意义
            fileEncoder = new FileContentEncoder();
        }
        FileEncoderTypeEnum fileEncoderType = fileEncoder.getFileEncoderType();
        if (PrivateDataUtils.isFileMaskFile(file)) {
            return;
        }
        if (chooseTypeEnum.equals(ChooseTypeEnum.FILE_ONLY)) {
            if (!isEncryptOperation || fileEncoderType.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
                computeBytes4ContentEncrypt(file, fileEncoderType, isEncryptOperation);
            }
            StatisticsService.scanFileTotalAmount++;
            return;
        } else if (chooseTypeEnum.equals(ChooseTypeEnum.CURRENT_DIR_ONLY)) {
            if (!isEncryptOperation || fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
                StatisticsService.scanFileTotalAmount++;
            }
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File one : files) {
                    if (one.isDirectory()) {
                        if (!isEncryptOperation || fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
                            StatisticsService.scanFileTotalAmount++;
                        }
                    } else {
                        if (!isEncryptOperation || fileEncoderType.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE)) {
                            computeBytes4ContentEncrypt(one, fileEncoderType, isEncryptOperation);
                        }
                        StatisticsService.scanFileTotalAmount++;
                    }
                }
            }
        } else if (chooseTypeEnum.equals(ChooseTypeEnum.CASCADE_DIR)) {
            // 收到停止命令
            if (StopCommandStatusService.getStopStatus().equals(StopCommandStatusService.STOP_STATUS_REQUIRE_STOP)) {
                return;
            }

            if (file.isDirectory()) {
                if (!isEncryptOperation || fileEncoderType.equals(FileEncoderTypeEnum.FILE_OR_DIR_NAME_ENCODE)) {
                    StatisticsService.scanFileTotalAmount++;
                }
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File one : files) {
                        if (!one.isDirectory() && (!isEncryptOperation || fileEncoderType.equals(FileEncoderTypeEnum.FILE_CONTENT_ENCODE))) {
                            computeBytes4ContentEncrypt(one, fileEncoderType, isEncryptOperation);
                        }
                        computeFileInfo(one, fileEncoder, chooseTypeEnum, isEncryptOperation);
                    }
                }
            } else {
                StatisticsService.scanFileTotalAmount++;
            }
        }
    }

    /**
     * 计算全文加密涉及的字节总数
     */
    private static boolean computeBytes4ContentEncrypt(File file, FileEncoderTypeEnum fileEncoderType, boolean isEncryptOperation) {
        if (!file.isDirectory()) {
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                boolean existsTail = TailService.existsTailModel(raf);
                //不存在
                if (!existsTail) {
                    if (isEncryptOperation) {
                        StatisticsService.todoFileTotalBytes = StatisticsService.todoFileTotalBytes + raf.length();
                    }
                }
                //存在
                else {
                    TailModel tailModel = TailService.getExistsTailModelInfo(raf);
                    boolean isCurrentUser = TailService.isCurrentUser(tailModel.getBelongUserMd516(), PasswordUtil.getMd51ForFileAuthentication());
                    boolean hasEncryptedByTypeOrConflict = TailService.isEncryptedByTypeOrConflict(tailModel, fileEncoderType);
                    byte[] encodeTypeFlagByte = tailModel.getEncodeType16();
                    byte flag = encodeTypeFlagByte[fileEncoderType.getPosition()];

                    //加密操作
                    if (isEncryptOperation) {
                        if (!hasEncryptedByTypeOrConflict && isCurrentUser) {
                            StatisticsService.todoFileTotalBytes = StatisticsService.todoFileTotalBytes + raf.length();
                        }
                    } else {
                        if (flag == TailService.ENCODED_FLAG && isCurrentUser) {
                            StatisticsService.todoFileTotalBytes = StatisticsService.todoFileTotalBytes + raf.length();
                        }
                    }
                }
            } catch (Exception ex) {
                log.info("计算过程中,文件打开失败", file.getPath());
                return true;
            }
        }
        return false;
    }

    /**
     * 返回操作类型对应的字符串
     */
    private static String getOperationType(boolean isEncryptOperation) {
        return isEncryptOperation ? "加密" : "解密";
    }

    /**
     * 判断选择路径是否合法
     * 为了系统安全, C:\ D:\ E:\ F:\ 这类长度太短的路径不支持加密
     */
    private static boolean isValidPath(String targetPath) {
        if (targetPath.length() <= 3) {
            return false;
        }
        if (!targetPath.contains("测试")) {
            return false;
        }
        return true;
    }
}
