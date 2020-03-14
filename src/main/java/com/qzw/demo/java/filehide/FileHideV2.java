package com.qzw.demo.java.filehide;

import lombok.Data;
import org.junit.Test;

import java.io.*;
import java.lang.invoke.ConstantCallSite;
import java.util.*;

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
 *
 * @author BG388892
 * @date 2019/12/31
 */
public class FileHideV2 {

    File dirMetaFile;
    File fileMetaFile;

    static String DIR_PREFIX = "nDDir";
    static String FILE_PREFIX = "nDFiLe";

    Long globalLong = 0L;

    static List<TwoTuple> dirList = new ArrayList<TwoTuple>();

    static List<TwoTuple> fileList = new ArrayList<TwoTuple>();

    static String SPLIT_WORD = ":==:";

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
        // 追加写
        FileOutputStream fos = new FileOutputStream(file, true);

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
        // todo 编码
        String oldPath = file.getPath();
        String newName = DIR_PREFIX + (globalLong++);

        String newPath = file.getPath().substring(0, file.getPath().lastIndexOf(File.separatorChar) + 1) + newName;
        if (Objects.equals(oldPath, targetDirPath)) {
            // 跳过目标文件夹
            return;
        }
        //todo qzw 命名失败
        try {
            file.renameTo(new File(newPath));
        } catch (Exception ex) {
            System.out.println(file.getPath() + "命名失败");
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

            //对文件夹进行处理
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
                one.renameTo(new File(newPath));
            } catch (Exception ex) {
                System.out.println(one.getPath() + "命名失败");
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
            //rename todo test renameTo when a file is reading 测试文件打开的时候重命名错误
            try {
                file.renameTo(new File(tuple.getOldPath()));
            } catch (Exception ex) {
                System.out.println(file.getPath() + "命名失败");
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
                System.out.println("元数据文件夹已经存在");
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
        //5 解密文件
        dirMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + dirMetaFileName);
        if (dirMetaFile.exists()) {
            deFiles(this.readTupleListFromMetaFile(fileMetaFile));
        } else {
            System.out.println("dirMeta文件不存在");
        }


        //6 解密文件夹(请注意, 顺序和加密文件夹要相反)
        fileMetaFile = new File(targetDirPath + File.separatorChar + metaDataDir + File.separatorChar + fileMetaFileName);

        if (fileMetaFile.exists()) {
            List<TwoTuple> dirList = this.readTupleListFromMetaFile(fileMetaFile);
            Collections.reverse(dirList);
            deFiles(dirList);
        } else {
            System.out.println("fileMetaFile不存在");
        }
    }

}

