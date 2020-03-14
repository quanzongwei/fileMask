package com.qzw.demo.java.filehide;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
 * @author BG388892
 * @incorrect 不会有这个问题, 因为文件的父路径, key和value都是使用文件夹加密后的路径
 * 命名加密: 加密过程, 不管是文件还是文件夹, 如果正在被使用(针对文件), 最多不会加密成功. 解密的时候也不会处理
 * 解密过程,如果文件正在被使用, 最多不会被解密(重命名失败), 此时可以重新执行一次. 如果解密没有成功, 可能会出问题.因为程序
 * 往下执行的时候, 父路径被修改了, 于是此时重命名永远会失败. 所以遇到失败的, 马上要报错停止执行, 然后提示: 文件解密不成功, 请关闭文件后重新解密
 * 解密不成功, 不允许重复加密
 * @correct 对于文件夹:理论上都是加密和解密成功的, 因为一般不会有其他进程在使用文件夹(重命名); 同时即使失败, 最多就是和原来的文件夹名称保持一致,日志多一条无效记录而已
 * 对于文件: key和value的父路径都是加密过后的文件夹路径, 允许加密不成功(重新执行加密操作即可); 解密不成功要抛异常, 提示用户
 * 关闭文件后重试; 特别是加密前解密操作必须是阻塞性的,否则将丢失原始文件名称信息
 * <p>
 * 不存在不处理,打印日志
 * <p>
 * 最快的加密文件和解密文件的工具
 * @date 2019/12/31
 */
@Slf4j
public class FileHideV4 {

    File dirMetaFile;
    File fileMetaFile;

    static String DIR_PREFIX = "nDDir";
    static String FILE_PREFIX = "nDFiLe";

    Long globalLong = 0L;

    static List<TwoTuple> dirList = new ArrayList<TwoTuple>();

    static List<TwoTuple> fileList = new ArrayList<TwoTuple>();

    static String SPLIT_WORD = ":!@:";// 断言,世界上文件名称中不会有这么奇葩的命名

    //    static String targetDirPath = "D:\\Data\\filehide - origin - 副本";
    static String targetDirPath = "D:\\Data\\a";
    static String dirMetaFileName = "dirMeta.txt";
    static String fileMetaFileName = "fileMeta.txt";
    static String metaDataDir = ".FileMask";


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
                System.out.println(file.getPath() + "加密文件夹, 重命名失败");
            }
        } catch (Exception ex) {
            System.out.println(file.getPath() + "加密文件夹,重命名失败" + ex.toString());
            System.out.println(ex);
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
            // 目前没有出现命名失败的情况
            try {
                boolean success = one.renameTo(new File(newPath));
                if (!success) {
                    System.out.println(file.getPath() + "加密文件, 重命名失败");
                }
            } catch (Exception ex) {
                System.out.println(file.getPath() + "加密文件, 重命名失败");
                System.out.println(ex);
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
     */
    private void deFiles(List<TwoTuple> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (TwoTuple tuple : list) {
            File file = new File(tuple.getNewPath());
            if (!file.exists()) {
                System.out.println("解密过程中, 文件不存在:" + file.getPath());
                continue;
            }
            try {
                boolean success = file.renameTo(new File(tuple.getOldPath()));
                if (!success) {
                    System.out.println(file.getPath() + "解密文件或文件夹, 重命名失败");
                }
            } catch (Exception ex) {
                System.out.println(file.getPath() + "解密文件或文件夹, 重命名失败");
                System.out.println(ex);
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
     * 重命名方法
     * 1. 目前没抛出过异常
     * 2. 传成功返回true
     * 3. 失败返回false,如果文件正在被word使用, 则返回false, 重命名失败; 如果是文本文件, notepad打开, name会重命名成功
     */
    @Test
    public void testOpenRename() {
        File file = new File("D:\\Data\\a\\admin.txt");
        boolean success = file.renameTo(new File("D:\\Data\\a\\admin2.txt"));

        System.out.println();
    }


    @Test
    public void testEn() throws IOException {
        Long t1 = System.currentTimeMillis();

        //生成加密文件夹
        File file = new File(targetDirPath + File.separatorChar + metaDataDir);
        if (!file.exists()) {
            file.mkdir();
            if (file.exists()) {
                System.out.println("元数据文件夹创建成功!");
            }
            System.out.println(file.getPath());
            System.out.println(file.getAbsoluteFile());
            Runtime.getRuntime().exec("attrib " + "\"" + file.getAbsolutePath() + "\"" + " +H");
        }

        dirMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + dirMetaFileName);
        fileMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + fileMetaFileName);


        File f = new File(targetDirPath);
        if (!f.exists()) {
            System.out.println("文件不存在:" + f.getPath());
            return;
        }

        //0 先解密testDe();
        // 1加密文件夹
        enDir(new File(targetDirPath));
        //2 写入文件夹元数据
        writeDirMapToSameDir(dirList, dirMetaFile);
        //3 加密文件
        enFiles(new File(targetDirPath));
        //4 写入文件元数据
        writeDirMapToSameDir(fileList, fileMetaFile);

        System.out.println("耗时ms:" + String.valueOf(System.currentTimeMillis() - t1));
    }

    @Test
    public void testDe() throws IOException {
        // 解密文件
        fileMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + fileMetaFileName);

        if (fileMetaFile.exists()) {
            deFiles(this.readTupleListFromMetaFile(fileMetaFile));

        } else {
            System.out.println("fileMetaFile不存在");
        }
        //6 解密文件夹(请注意, 顺序和加密文件夹要相反)
        dirMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + dirMetaFileName);
        if (dirMetaFile.exists()) {
            List<TwoTuple> dirList = this.readTupleListFromMetaFile(dirMetaFile);
            Collections.reverse(dirList);
            deFiles(dirList);
        } else {
            System.out.println("dirMeta文件不存在");
        }

    }

    public void encryptFile(String targetDirPath) {

    }


    @Test
    public void testA() {
        log.info("你好世界");
        System.out.println("你好世界诶");
    }
}

// 文件重名了咋办