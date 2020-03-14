package com.qzw.demo.filemask;

import com.qzw.demo.java.filemask.exception.MaskException;
import org.junit.Before;

import java.io.*;

/**
 * @author BG388892
 * @date 2020/1/22
 */
public class BaseTest {

    protected File getBaseFile() {
        File file = new File("D:\\Data\\测试\\test");
        return file;
    }

    protected File firstDirFile() {
        File file = new File("D:\\Data\\测试\\test\\dir");
        return file;
    }

    protected File firstFileFile() {
        File file = new File("D:\\Data\\测试\\test\\dir\\file.txt");
        return file;
    }

    protected File firstDirFileEncryptedByType1(int number) {
        File file = new File("D:\\Data\\测试\\test\\nDDir" + number);
        return file;
    }

    @Before
    public void setUp() {

    }

    public static char sp = File.separatorChar;
    String text = "admin中华人民共和国admin中华人民共和国admin中华人民共和国admin中华人民共和国admin中华人民共和国admin中华人民共和国";

    /**
     * 创建level层级的文件, 除了第一层为dir一个文件夹之外, 其他都是两个一个dir文件夹,一个file文件.
     *
     * @return 文件和文件夹总数, 包括base文件夹下的第一个dir
     */
    protected int createDirAndFile(String path, int level) throws IOException {
        boolean isFirstLevel = true;
        int returnValue = (level - 1) * 2 + 1 - 1;
        while (level-- > 0) {
            File dir = new File(path + sp + "dir");
            dir.mkdir();
            if (!isFirstLevel) {
                File file = new File(path + sp + "file.txt");
                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
                    bw.write(text);
                }
            }

            path = path + sp + "dir";
            isFirstLevel = false;
        }
        return returnValue;
    }

    // 文件夹为dir,文件名为file.txt, 则正常
    protected void validateFileNameTrue(File file) throws IOException {
        if (isFileMaskFile(file)) {
            return;
        }
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File one : files) {
                if (one.isDirectory()) {
                    if (isFileMaskFile(one)) {
                        continue;
                    }
                    if (!one.getName().equals("dir")) {
                        throw new MaskException(1000, "文件名称不正确,测试失败" + one.getPath());
                    }
                    validateFileNameTrue(one);
                    continue;
                }
                if (!one.getName().equals("file.txt")) {
                    throw new MaskException(1000, "文件名称不正确,测试失败" + one.getPath());
                }
            }
        }
        if (!file.getName().equals("dir")) {
            throw new MaskException(1000, "文件名称不正确,测试失败" + file.getPath());
        }
    }

    boolean isFileMaskFile(File file) {
        if (file.getPath().contains(".fileMask")) {
            return true;
        }
        return false;
    }

    // 文件夹不为dir,文件名不为file.txt, 则正常
    protected void validateFileNameFalse(File file) throws IOException {

        if (isFileMaskFile(file)) {
            return;
        }
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File one : files) {
                if (one.isDirectory()) {
                    if (isFileMaskFile(one)) {
                        continue;
                    }
                    if (one.getName().equals("dir")) {
                        throw new MaskException(1000, "文件名称不正确,测试失败" + one.getPath());
                    }
                    validateFileNameFalse(one);
                    continue;
                }
                if (one.getName().equals("file.txt")) {
                    throw new MaskException(1000, "文件名称不正确,测试失败" + one.getPath());
                }
            }
        }
        if (file.getName().equals("dir")) {
            throw new MaskException(1000, "文件名称不正确,测试失败" + file.getPath());
        }
    }


    //todo
    protected void validateFileContent(File file) throws IOException {
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File one : files) {
                if (one.isDirectory()) {
                    validateFileContent(one);
                    continue;
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(one)));
                if (!br.readLine().equals(text)) {
                    throw new MaskException(1000, "文件内容不正确,测试失败" + one.getPath());

                }
            }
        }
    }

    // 级联删除文件夹下所有数据 不包含外面的文件夹
    protected void removeFileAndDir(File file, boolean isFirst) {
        if (!file.getPath().contains("测试")) {
            throw new MaskException(10000, "费测试文件禁止删除");
        }
        File[] files = file.listFiles();
        if (files != null && files.length > 0) {
            for (File one : files) {
                if (one.isDirectory()) {
                    removeFileAndDir(one, false);
                }
                one.delete();
            }
        }
        if (!isFirst) {
            file.delete();
        }
    }
}
