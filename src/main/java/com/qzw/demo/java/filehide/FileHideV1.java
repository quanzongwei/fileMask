package com.qzw.demo.java.filehide;

import lombok.Data;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文本数据结构oldPath:==:newPath
 * <p>
 * 单个文件
 *
 * @author BG388892
 * @date 2019/12/31
 */
public class FileHideV1 {

    static Long fileCount = 0L;
    static Long dirCount = 0L;
    static Long totalByte = 0L;

    File dirMeta;
    File fileMeta;

    static String DIR_PREFIX = "nDDir";
    static String FILE_PREFIX = "nDFiLe";

    Long globalLong = 0L;

    static List<TwoTuple> dirList = new ArrayList<TwoTuple>();

    static List<TwoTuple> fileList = new ArrayList<TwoTuple>();

    static String SPLIT_WORD = ":==:";
    //起始文件夹的新名字
    static String beginDirNewName = null;


    //    @Test
    public void test() {

        Long t1 = System.currentTimeMillis();
        String filePath = "D:\\Data\\filehide";

        dirMeta = new File("D:\\Data\\filehide\\dirMeta.txt");
        fileMeta = new File("D:\\Data\\filehide\\fileMeta.txt");

        countFileNameTotalByte(filePath);
        System.out.println("fileCount:0,dirCount:1".replaceFirst("0", String.valueOf(fileCount)).replaceFirst("1", String.valueOf(dirCount)));
        System.out.println("total byte: " + totalByte);
        System.out.println("耗时ms:" + String.valueOf(System.currentTimeMillis() - t1));
    }

    /**
     * 加密解密都是幂等的
     */
    @Test
    public void testEnDirAndFIles() throws IOException {

        Long t1 = System.currentTimeMillis();
        String filePath = "D:\\Data\\filehide";

        dirMeta = new File("D:\\Data\\dirMeta.txt");
        fileMeta = new File("D:\\Data\\fileMeta.txt");
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("文件不存在:" + f.getPath());
            return;
        }
//        countFileNameTotalByte(filePath);
        // 1加密文件夹
        enDir(new File(filePath));
        //2 写入文件夹元数据 todo null map set
        writeDirMapToSameDir(dirList, dirMeta);
        //3 加密文件, 请注意此时其实文件件的名称已经变了
        enFiles(new File(filePath));
        //4 写入文件元数据
        writeDirMapToSameDir(fileList, fileMeta);


        System.out.println("fileCount:0,dirCount:1".replaceFirst("0", String.valueOf(fileCount)).replaceFirst("1", String.valueOf(dirCount)));
        System.out.println("total byte: " + totalByte);
        System.out.println("耗时ms:" + String.valueOf(System.currentTimeMillis() - t1));
    }


    @Test
    public void testEn() throws IOException {
        Long t1 = System.currentTimeMillis();
        String filePath = "D:\\Data\\filehide";

        dirMeta = new File("D:\\Data\\dirMeta.txt");
        fileMeta = new File("D:\\Data\\fileMeta.txt");
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("文件不存在:" + f.getPath());
            return;
        }
        // 1加密文件夹
        enDir(new File(filePath));
        //2 写入文件夹元数据 todo null map set
        writeDirMapToSameDir(dirList, dirMeta);
        //3 加密文件
        enFiles(new File(beginDirNewName));
        //4 写入文件元数据
        writeDirMapToSameDir(fileList, fileMeta);

        System.out.println("fileCount:0,dirCount:1".replaceFirst("0", String.valueOf(fileCount)).replaceFirst("1", String.valueOf(dirCount)));
        System.out.println("total byte: " + totalByte);
        System.out.println("耗时ms:" + String.valueOf(System.currentTimeMillis() - t1));
    }

    @Test
    public void testDe() throws IOException {
        dirMeta = new File("D:\\Data\\dirMeta.txt");
        fileMeta = new File("D:\\Data\\fileMeta.txt");
        //5 解密文件
        deFiles(this.readTupleListFromMetaFile(fileMeta));
        //6 解密文件夹
        deFiles(this.readTupleListFromMetaFile(dirMeta));
    }


    /**
     * @param list dirMap或者fileMap
     * @param file DirMeta或者fileMEta
     */
    private void writeDirMapToSameDir(List<TwoTuple> list, File file) throws IOException {
        if (list == null || list.isEmpty()) {
            return;
        }
        FileOutputStream fos = new FileOutputStream(file);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        //todo Exception
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
        List<TwoTuple> list = new ArrayList<TwoTuple>();
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
//        String newName = DIR_PREFIX + UUID.randomUUID().toString().replaceAll("-", "");
        String newName = DIR_PREFIX + (globalLong++);

        String newPath = file.getPath().substring(0, file.getPath().lastIndexOf(File.separatorChar) + 1) + newName;
        //todo qzw 命名失败
        try {
            file.renameTo(new File(newPath));
        } catch (Exception ex) {
            System.out.println(file.getPath() + "命名失败");
            System.out.println(ex);
        }
        // map中存了失败的数据,但是永远不会被使用
        dirList.add(new TwoTuple(oldPath, newPath));
        //todo refactor
        if (oldPath.equals("D:\\Data\\filehide")) {
            beginDirNewName = newPath;
        }
    }


    /**
     * @param file
     */
    private void enFiles(File file) {
        if (file == null || !file.isDirectory()) {
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
            // todo 编码
            // todo qzw 命名失败
            try {
                one.renameTo(new File(newPath));
            } catch (Exception ex) {
                System.out.println(one.getPath() + "命名失败");
                System.out.println(ex);
            }
            // map中存了失败的数据,但是永远不会被使用
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

    private void countFileNameTotalByte(String path) {
        File file = new File(path);

        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list == null || list.length == 0) {
                return;
            }
            for (File f : list) {

                if (f.isDirectory()) {
                    dirCount = dirCount + 1;
                    totalByte = totalByte + f.getPath().getBytes().length;
                    countFileNameTotalByte(f.getPath());
                } else {
                    fileCount = fileCount + 1;
                    totalByte = totalByte + f.getPath().getBytes().length;
                }

            }
        }
    }


    /**
     * 测试结果
     * 文件夹重命名仅支持当前文件夹下重命名
     * <p>
     * 重命名之后原来的file.listFiles()==null
     * <p>
     * getName="a.jar"
     * getPath="D:\\Data\\filehide\\a.jar"
     * <p>
     * 超长文件名不能删除, 删除被视为移到回收站, 但是回收站不接受这么长的字符串
     * <p>
     * getDirectory D:\ad\
     * getFile   admin.txt
     */
//    @Test
    public void testRename() {
//        String filePath = "D:\\Data\\filehide\\a.jar";
        String filePath = "D:\\Data\\filehide";

        File file = new File(filePath);
        file.renameTo(new File("D:\\Data\\filehide2"));
        System.out.println();


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

}

