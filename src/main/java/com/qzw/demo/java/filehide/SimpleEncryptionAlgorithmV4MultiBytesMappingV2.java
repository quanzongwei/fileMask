package com.qzw.demo.java.filehide;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 2多字节映射算法
 * 再简化, 单字节表示256(2^16)种情况,那就是256!, 双字节是2^16=65536,那就是65536!
 * 单字节随机映射已经是无法破解的了
 * <p>
 * 请写一篇文章, 论述一下这么精妙的算法
 * <p>
 * 可行性测试已经通过
 * v2: 随机函数优化,工程化
 *
 * @author BG388892
 * @date 2020/1/16
 */
public class SimpleEncryptionAlgorithmV4MultiBytesMappingV2 {

    public static String rd1 = "qqqq-qqqq-qqqq-qqqq-qqqq-qqqq-q1";
    public static String rd2 = "aaaa-aaaa-aaaa-aaaa-aaaa-aaaa-q2";
    public static String rd3 = "bbbb-bbbb-bbbb-bbbb-bbbb-bbbb-q3";
    public static String rd4 = "cccc-cccc-cccc-cccc-cccc-cccc-q4";
    public static String rd5 = "dddd-dddd-dddd-dddd-dddd-dddd-q5";
    public static String rd6 = "eeee-eeee-eeee-eeee-eeee-eeee-q6";
    public static String rd7 = "ffff-ffff-ffff-ffff-ffff-ffff-q7";
    public static String rd8 = "gggg-gggg-gggg-gggg-gggg-gggg-q8";

    // 字节数组大小 32字节 ,size=8
    //encodeMap.size=decodeMap.size = 256
    private void generateMap(byte[] encodeMap, byte[] decodeMap, List<byte[]> random) {
        if (random.size() != 8) {
            throw new RuntimeException("参数错误");
        }
        // 取值集合
        List<Byte> bList = new ArrayList<>();
        List<Byte> mappedList = new ArrayList<>();

        for (Integer i = 0; i < 256; i++) {
            bList.add(i.byteValue());
        }
        // 8个32字节的随机数, 一共256字节
        for (byte[] b : random) {
            for (int i = 0; i < 32; i++) {
                byte b2 = b[i];
                if (bList.contains(b2)) {
                    mappedList.add(b2);
                    bList.remove(bList.indexOf(b2));
                    bList = bList.stream().collect(Collectors.toList());

                } else {
                    // 冲突解决函数待优化 todo
                    mappedList.add(bList.get(0));
                    // todo remove bug? 确实remove可能会出问题
                    bList.remove(0);
                    bList = bList.stream().collect(Collectors.toList());

                }
            }
        }
        for (int i = 0; i < mappedList.size(); i++) {
            encodeMap[i] = mappedList.get(i);
            decodeMap[ByteUtil.getUnsignedByte(mappedList.get(i))] = (byte) i;

        }
    }

    private void generateMapV2(byte[] encodeMap, byte[] decodeMap) {
        Random rd = new Random();
        // todo 随机
        rd.setSeed(System.currentTimeMillis());
        byte[] rdBytes = new byte[256];
        rd.nextBytes(rdBytes);
        List<Byte> bList = new ArrayList<>();
        for (Integer i = 0; i < 256; i++) {
            bList.add(i.byteValue());
        }
        List<Byte> mappedList = new ArrayList<>();
        for (int i = 0; i < rdBytes.length; i++) {
            byte b = rdBytes[i];
            if (bList.contains(b)) {
                mappedList.add(b);
                bList.remove(bList.indexOf(b));
                bList = bList.stream().collect(Collectors.toList());
            } else {
                // conflict resolve
                int index = rd.nextInt(bList.size());
                mappedList.add(bList.get(index));
                bList.remove(index);
                bList = bList.stream().collect(Collectors.toList());
            }
        }
        for (int i = 0; i < mappedList.size(); i++) {
            encodeMap[i] = mappedList.get(i);
            decodeMap[ByteUtil.getUnsignedByte(mappedList.get(i))] = (byte) i;

        }
    }

    private void encodeOrDecodeFile(File file, boolean isEncodeOperation) throws IOException {
        byte[] encodeMap = new byte[256];
        byte[] decodeMap = new byte[256];
        generateMapV2(encodeMap, decodeMap);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            long length = raf.length();
            long blockNum = length / 1024;
            Long remain = length % 1024;

            for (long i = 0; i < blockNum; i++) {
                byte[] b1 = new byte[1024];
                raf.seek(0 + i * 1024);
                raf.read(b1, 0, 1024);
                for (int j = 0; j < b1.length; j++) {
                    if (isEncodeOperation) {
                        b1[j] = encodeMap[ByteUtil.getUnsignedByte(b1[j])];
                    } else {
                        b1[j] = decodeMap[ByteUtil.getUnsignedByte(b1[j])];
                    }
                }
                raf.seek(0 + i * 1024);
                raf.write(b1);
            }
            // 尾部数据处理
            if (remain > 0) {
                byte[] b3 = new byte[remain.intValue()];
                raf.seek(0 + blockNum * 1024);
                raf.read(b3, 0, remain.intValue());
                for (int j = 0; j < b3.length; j++) {
                    if (isEncodeOperation) {
                        b3[j] = encodeMap[ByteUtil.getUnsignedByte(b3[j])];
                    } else {
                        b3[j] = decodeMap[ByteUtil.getUnsignedByte(b3[j])];
                    }
                }
                raf.seek(0 + blockNum * 1024);
                raf.write(b3);
            }
        }
    }


    //100M耗时1s
    //1G耗时7s
    //12G: 耗时248884, 240s, 4分钟, 和xor的性能一模一样
    // 支持多次加密->然后多次解密得到结果, 这点很重要啊
    @Test
    public void testEncode() throws IOException {
        long begin = System.currentTimeMillis();
//        File file = new File("D:\\Data\\测试\\PPT资料包 - 副本.rar");
//        File file = new File("D:\\Data\\测试\\网龙-整理.rar");
        File file = new File("D:\\Data\\测试\\xiaomage-space-master-769283dc49ebe7e0fe8af1d277e21f4dcb930e20.zip");
        encodeOrDecodeFile(file, true);
        System.out.println("耗时:" + (System.currentTimeMillis() - begin));
    }

    //100M: 耗时1s
    //12G: 212864 210s=4分钟
    @Test
    public void testDecode() throws IOException {
        long begin = System.currentTimeMillis();
//        File file = new File("D:\\Data\\测试\\PPT资料包 - 副本.rar");
        File file = new File("D:\\Data\\测试\\网龙-整理.rar");
//        File file = new File("D:\\Data\\测试\\xiaomage-space-master-769283dc49ebe7e0fe8af1d277e21f4dcb930e20.zip");

        encodeOrDecodeFile(file, false);
        System.out.println("耗时:" + (System.currentTimeMillis() - begin));
    }

}
