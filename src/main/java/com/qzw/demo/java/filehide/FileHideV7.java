package com.qzw.demo.java.filehide;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 文本数据结构oldPath:==:newPath
 * <p>
 * 文件支持追加内容
 * <p>
 * 给一个dir就行
 * <p>
 * 幂等性, 追加写
 * <p>
 * 加密解密
 * <p>
 * 建议: 加密的时候, 文件不要被使用
 * <p>
 * 编码问题, 应该都是转为UTF-8,然后又转回去
 * 覆盖写的话, 会有问题. 计数从0开始了, 可能会重复; 所以加上逻辑: 先解密再加密, 覆盖写源文件数据
 * <p>
 * 禁止对整个盘加密, 这个是个危险操作
 *
 * @error 不会有这个问题, 因为文件的父路径, key和value都是使用文件夹加密后的路径
 * 命名加密: 加密过程, 不管是文件还是文件夹, 如果正在被使用(针对文件), 最多不会加密成功. 解密的时候也不会处理
 * 解密过程,如果文件正在被使用, 最多不会被解密(重命名失败), 此时可以重新执行一次. 如果解密没有成功, 可能会出问题.因为程序
 * 往下执行的时候, 父路径被修改了, 于是此时重命名永远会失败. 所以遇到失败的, 马上要报错停止执行, 然后提示: 文件解密不成功, 请关闭文件后重新解密
 * 解密不成功, 不允许重复加密
 * @correct 对于文件夹:理论上都是加密和解密成功的, 因为一般不会有其他进程在使用文件夹(重命名); 同时即使失败, 最多就是和原来的文件夹名称保持一致,日志多一条无效记录而已
 * 对于文件: key和value的父路径都是加密过后的文件夹路径, 允许加密不成功(重新执行加密操作即可); 解密不成功要抛异常, 提示用户
 * 关闭文件后重试; 特别是加密前解密操作必须是阻塞性的,否则将丢失原始文件名称信息.
 * 文件加密的失败没问题, 文件解密的失败,重复加密后会永远丢失原来的名字,所以这时候的rename必须成功
 * <p>
 * <p>
 * 不存在不处理,打印日志
 * <p>
 * 最快的加密文件和解密文件的工具
 * V5: 文件解密抛出阻塞性异常
 * v6: log4j, 全量操作, 就是怕增量会有文件名称冲突012
 * v7: 保留v6中的一些例子
 * @date 2019/12/31
 */
@Log4j2
public class FileHideV7 {

    private File dirMetaFile;
    private File fileMetaFile;

    private static String DIR_PREFIX = "nDDir";
    private static String FILE_PREFIX = "nDFiLe";

    private Long globalLong = 0L;

    private static List<TwoTuple> dirList = new ArrayList<TwoTuple>();

    private static List<TwoTuple> fileList = new ArrayList<TwoTuple>();

    /**
     * 断言,世界上的文件名称中不会有这么奇葩的命名
     */
    private static String SPLIT_WORD = ":!@:";

    private static String targetDirPath = "D:\\Data\\a";
    private static String dirMetaFileName = "dirMeta.txt";
    private static String fileMetaFileName = "fileMeta.txt";
    private static String metaDataDir = ".FileMask";


    /**
     * @param list dirList或者fileList
     * @param file DirMeta或者fileMEta
     */
    private void writeDirMapToSameDir(List<TwoTuple> list, File file) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        // 覆盖写
        FileOutputStream fos = new FileOutputStream(file, false);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (TwoTuple tuple : list) {
            bw.write(tuple.getOldPath());
            bw.write(SPLIT_WORD);
            bw.write(tuple.getNewPath());
            bw.newLine();
        }
        bw.close();
        fos.close();
    }


    /**
     * old->new
     */
    private List<TwoTuple> readTupleListFromMetaFile(File file) throws IOException {
        List<TwoTuple> list = new ArrayList<>();
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(SPLIT_WORD);
            if (split.length == 2) {
                TwoTuple tuple = new TwoTuple(split[0], split[1]);
                list.add(tuple);
            }
        }

        br.close();
        fis.close();
        return list;
    }

    /**
     * 深度优先遍历
     *
     * @param file
     */
    private void enDir(File file) {
        if (file == null || !file.isDirectory()) {
            return;
        }
        if (file.getName().equals(metaDataDir)) {
            // 加密元数据文件夹不处理
            return;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        for (File one : files) {
            // 不处理文件
            if (!one.isDirectory()) {
                continue;
            }

            //对文件夹进行处理
            String name = one.getName();
            if (name.startsWith(DIR_PREFIX)) {
                //已经处理过的不继续处理, 幂等性处理, 允许多次执行
                continue;
            }

            enDir(one);
        }
        String oldPath = file.getPath();
        String newName = DIR_PREFIX + (globalLong++);

        String newPath = file.getPath().substring(0, file.getPath().lastIndexOf(File.separatorChar) + 1) + newName;
        if (Objects.equals(oldPath, targetDirPath)) {
            // 跳过目标文件夹
            return;
        }
        // 一般情况下不会命名失败
        try {
            boolean success = file.renameTo(new File(newPath));
            if (!success) {
                log.info(file.getPath() + "加密文件夹, 重命名失败");
            }
        } catch (Exception ex) {
            log.info(file.getPath() + "加密文件夹,重命名失败" + ex.toString(), ex);
        }
        // list中可能存在失败的数据, 但是永远不会被使用。根文件夹不做加密处理
        dirList.add(new TwoTuple(oldPath, newPath));
    }


    /**
     * @param file
     */
    private void enFiles(File file) {
        if (file == null || !file.isDirectory()) {
            return;
        }
        if (file.getName().equals(metaDataDir)) {
            // 加密元数据文件夹不处理
            return;
        }

        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        for (File one : files) {
            if (one.isDirectory()) {
                enFiles(one);
                continue;
            }
            //对文件进行处理
            String name = one.getName();
            if (name.startsWith(FILE_PREFIX)) {
                //已经处理过的不继续处理, 幂等性处理, 允许多次执行
                continue;
            }
            //文件处理
            String oldPath = one.getPath();
            String newName = FILE_PREFIX + (globalLong++);
            String newPath = one.getParent() + File.separatorChar + newName;
            // 目前a目录下打开docx b, 那么a文件夹和b文件都会重命名失败; 但是不影响大局, 子文件夹加密依然是成功的
            try {
                boolean success = one.renameTo(new File(newPath));
                if (!success) {
                    log.info(one.getPath() + "加密文件, 重命名失败");
                }
            } catch (Exception ex) {
                log.info(file.getPath() + "加密文件, 重命名失败", ex);
            }
            // list中存了失败的数据,但是永远不会被使用
            fileList.add(new TwoTuple(oldPath, newPath));
        }
    }

    /**
     * 文件的解密操作是没有顺序的
     * 但是文件夹的解密操作是有顺序的
     * 都按照加密顺序解密就好了
     * <p>
     * 解密顺序
     * 1. 文件
     * 2.文件夹
     * <p>
     * 支持幂等性
     *
     * @param isDirectory true: 文件夹操作 false:文件操作
     */
    private void deFiles(List<TwoTuple> list, boolean isDirectory) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (TwoTuple tuple : list) {
            File file = new File(tuple.getNewPath());
            if (!file.exists()) {
                log.info("解密过程中, 文件不存在:" + file.getPath());
                continue;
            }
            try {
                boolean success = file.renameTo(new File(tuple.getOldPath()));
                if (!success) {
                    log.info(file.getPath() + "解密文件或文件夹, 重命名失败");
                    if (!isDirectory) {
                        throw new FileHideException(FileHideException.FILE_RENAME_ERROR, "文件:{0}正在被使用, 请关闭对应的应用程序后, 重新执行解密操作".replaceFirst("\\{0}", file.getPath()));
                    }
                }
            } catch (Exception ex) {
                log.info(file.getPath() + "解密文件或文件夹, 重命名失败", ex);
                if (!isDirectory) {
                    throw new FileHideException(FileHideException.FILE_RENAME_ERROR, "文件:{0}正在被使用, 请关闭对应的应用程序后, 重新执行解密操作".replaceFirst("\\{0}", file.getPath()));
                }
            }
        }
    }

    @Data
    public static class TwoTuple {
        String newPath;
        String oldPath;

        public TwoTuple(String oldPath, String newPath) {
            this.oldPath = oldPath;
            this.newPath = newPath;
        }
    }


    /**
     * 加密
     *
     * @throws FileHideException 解密文件正在被使用的时候报错, 文件不存在错误不会发生,因为是从文件管理器选择的
     */
    public void encrypt(String targetDirPath) throws IOException {
        FileHideV7.targetDirPath = targetDirPath;
        File f = new File(targetDirPath);
        if (!f.exists()) {
            log.info("文件不存在:" + f.getPath());
            throw new FileHideException(FileHideException.TARGET_DIR_NOT_EXIST, "目标文件夹不存在,请重新选择");
        }
        log.info("======加密操作begin======");
        log.info("文件夹加密过程开始");
        Long t1 = System.currentTimeMillis();
        //生成加密文件夹
        File file = new File(targetDirPath + File.separatorChar + metaDataDir);
        if (!file.exists()) {
            file.mkdir();
            if (file.exists()) {
                log.info("元数据文件夹创建成功!");
            }
            log.info(file.getPath());
            log.info(file.getAbsoluteFile().toString());
            Runtime.getRuntime().exec("attrib " + "\"" + file.getAbsolutePath() + "\"" + " +H");
        }


        dirMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + dirMetaFileName);
        fileMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + fileMetaFileName);

        //0 先解密
        log.info("加密之前先执行解密");
        try {
            decrypt(targetDirPath);
        } catch (FileHideException e) {
            if (e.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                log.info("解密失败, 终止加密过程,errorMsg" + e.getMessage());
                log.info("======end======");
                throw e;
            }
        }
        log.info("解密操作成功");
        //1加密文件夹
        enDir(new File(targetDirPath));
        //2 写入文件夹元数据
        writeDirMapToSameDir(dirList, dirMetaFile);
        //3 加密文件
        enFiles(new File(targetDirPath));
        //4 写入文件元数据
        writeDirMapToSameDir(fileList, fileMetaFile);
        log.info("加密操作成功, 耗时ms:" + String.valueOf(System.currentTimeMillis() - t1));
        log.info("======加密操作end======");

    }

    /**
     * 解密
     *
     * @throws FileHideException 解密文件正在被使用的时候报错
     */
    public void decrypt(String targetDirPath) throws IOException {
        Long t1 = System.currentTimeMillis();
        log.info("======解密操作begin======");
        if (targetDirPath == null || !(new File(targetDirPath).exists())) {
            throw new FileHideException(FileHideException.TARGET_DIR_NOT_EXIST, "目标文件夹不存在,请重新选择");
        }
        FileHideV7.targetDirPath = targetDirPath;
        // 解密文件
        fileMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + fileMetaFileName);
        if (fileMetaFile.exists()) {
            try {
                deFiles(this.readTupleListFromMetaFile(fileMetaFile), false);
            } catch (FileHideException e) {
                if (e.getCode().equals(FileHideException.FILE_RENAME_ERROR)) {
                    log.info("解密失败, 终止加密过程,errorMsg: " + e.getMessage());
                    throw e;
                }
            }
        } else {
            log.info("fileMetaFile不存在, 没有需要解密的数据");
        }
        //6 解密文件夹(请注意, 顺序和加密文件夹要相反)
        dirMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + dirMetaFileName);
        if (dirMetaFile.exists()) {
            List<TwoTuple> dirList = this.readTupleListFromMetaFile(dirMetaFile);
            Collections.reverse(dirList);
            deFiles(dirList, true);
        } else {
            log.info("dirMeta文件不存在,没有需要解密的文件夹");
        }
        log.info("解密操作成功, 耗时ms:" + String.valueOf(System.currentTimeMillis() - t1));
        log.info("======解密操作end======");
    }

    /**
     * 如果文件夹下有文件被打开, 那么文件夹命名会失败,仅针对当前文件夹和当前文件
     *
     * @throws IOException
     */
    @Test
    @Ignore
    public void testEncrypt() throws IOException {
        String targetDir = "D:\\Data\\filehide - origin - 副本";
        new FileHideV7().encrypt(targetDir);
    }

    @Test
    @Ignore
    public void testDecrypt() throws IOException {
        String targetDir = "D:\\Data\\filehide - origin - 副本";
        new FileHideV7().decrypt(targetDir);
    }
}
