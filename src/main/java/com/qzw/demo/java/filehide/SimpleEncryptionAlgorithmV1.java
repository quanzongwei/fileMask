package com.qzw.demo.java.filehide;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * simple encryption algorithm
 * 简单加密算法, 算法简单但是位运算耗时,
 * 虽然我想出来了,但是人家早就想出来了
 * ref: https://en.wikipedia.org/wiki/XOR_cipher
 * 知识这个算法存在一些问题, 已知明文和密文的情况下, 能推测出密码, 如果逐个字节加密的话
 *
 * @date 2020/1/10
 */
public class SimpleEncryptionAlgorithmV1 {

    /**
     * @param originContent 长度和pass的长度相同
     * @param pass
     * @return
     */
    public static byte[] encrypt(byte[] originContent, byte[] pass) {

        byte[] encryptedContent = new byte[originContent.length];
        for (int i = 0; i < originContent.length; i++) {
            byte b1 = originContent[i];
            byte b2 = pass[i];

            byte b2Not = (byte) (~b2);

            byte b3 = (byte) (b1 ^ b2Not);
            encryptedContent[i] = b3;
        }
        return encryptedContent;
    }

    public static byte[] decrypt(byte[] encryptedContent, byte[] pass) {
        byte[] originContent = new byte[encryptedContent.length];
        for (int i = 0; i < encryptedContent.length; i++) {
            byte b1 = encryptedContent[i];
            byte b2 = pass[i];

            byte b2Not = (byte) (~b2);

            byte b3 = (byte) (b1 ^ b2Not);
            originContent[i] = b3;
        }
        return originContent;
    }

    //todo 1G 大文件加密
    @Test
    public void test() {
        Byte by = "a".getBytes()[0];
        System.out.println(by.toString());

    }

    /**
     * 原始字符串:LD9OdVE=字节长度:5
     * 原始密码:YzFrM0M=字节长度5
     * 加密后字符串的长度:5
     * 加密后的字符串:sPHaue0=
     * 解密后字符串的长度:5
     * 解密后的字符串:LD9OdVE=
     * <p>
     * 测试成功没问题
     */
    @Test
    public void test2() throws UnsupportedEncodingException {
        String s = RandomStringUtils.randomAscii(5);
        String pass = RandomStringUtils.randomAscii(5);
        System.out.println("原始字符串:" + Base64.getEncoder().encodeToString(s.getBytes("ASCII")) + "字节长度:" + s.getBytes("ASCII").length);
        System.out.println("原始密码:" + Base64.getEncoder().encodeToString(pass.getBytes("ASCII")) + "字节长度" + pass.getBytes("ASCII").length);
        byte[] encrypt = encrypt(s.getBytes("ASCII"), pass.getBytes("ASCII"));
        System.out.println("加密后字符串的长度:" + encrypt.length);
        System.out.println("加密后的字符串:" + Base64.getEncoder().encodeToString(encrypt));
        byte[] decrypt = decrypt(encrypt, pass.getBytes("ASCII"));
        System.out.println("解密后字符串的长度:" + decrypt.length);
        System.out.println("解密后的字符串:" + Base64.getEncoder().encodeToString(decrypt));
    }

    public static byte[] str = RandomStringUtils.randomAscii(1024).getBytes();
    static String key = "4lZczuj)TspQij4+Mg\"+$[fRDNA<x'?]eb'32 Gcq@6Er~Mq,I]D\\[Z+w/4?<O}NyHm1^+P342Oz=N-c0ff0]}l$sGd{J'7^UTcoP*>o dImJ`;'ZGt7_=FArFXkWDgIRkE>R7[AI]&PN?RvW=sA!'U @xw7%PhVp4E^kTD3R;)zAFb5QX7QsR:dUVJ\\1';8JTG:$UDgg3:;t_1g}4Js+!2fkPm\"`[x:3O{s)eDjq<i1s%#,=c1We?TAve~]KYYH|sP=gg:]n$ZQh6L^6w2DP4q-|7'~e+=pr\"E^l#ghNBWz~6E%HI(!m/{/@L8!_wNmJ{22Nz%|BWt2ya' Yt%a2LehfZjRQB`?>YVZ'\"%\":-pWg<o~qa/(+.2'bQ@YsD[@B1%; )Ty:1AHPL28Nr\\R\"=/a3*YejR\\L`7l^}J|Q$ cEFJmb)L#Tnh?XSOwLg?TS&,X]hd%~tR\\c;`k`#f\"p*Q09Hc0\"a1ply!t)`t0:*>Cc,DxR/{7_veoA<\"|F|qDW>>fV->ZrnvCfw\\B3/8c#ke'5||j?qf1wkf,yl82/PM;cT[#i{j>:a4A9Bjj$U2,F_`-/P|0:Y#d\"s1Y\\a9|[;aY}Fm+?dyF3<72>1\"OP+luz*l1#qKdUm(EeW0.}tcv;@cJB08,5(wPO'e]x}-C*FClI+?K8+[dO>R@Qw%nB5~-`&UL;Y?J[1eQ#v\"(N7szA.W}#o,\"9L0+IB$B|KMP,^VjV=$!C:M.>ePpoGT$N:VmfD1EV;GRShqR.IU+jQr%lyeDTwQ_HA1=JYj|;fv#eFY5mL&aNjaR{6jn-!;f9pt\\J)*-Lq#'R``4iGhpO?l2mxIx<o[9j,1I5azqEfB$8MLP'i|Z! ~6G#p +JuZ.py~.l!YNXq6X_1 Buh^K\"([6bw`jQ+(yZsnn-_o~8EElqvUm&Lebbpll=90@-x^ /|]E`o {}/o,e*-r4ZdMF.g,!Q2\\8h\">{r`s]<5jWu`kk3kI`f]:z!:m:LF2%Fy+<jl**BNspTd%vHy43J2y>}vq";

    /**
     * 100M: 耗时1s
     * 11G: 耗时250 s
     * <p>
     * 文件长度:12287384528
     * 文件块数:11999398
     * 1G加密耗时:250206 ms, 4分钟加密1个G
     * <p>
     * 对称加密迎来崭新的时代
     */
    @Test
    public void test加密100M() throws IOException {
//        try (RandomAccessFile raf =  new RandomAccessFile(new File("D:\\Data\\网龙-整理.rar"), "rw")) {
        try (RandomAccessFile raf = new RandomAccessFile(new File("D:\\Data\\网龙-整理.rar"), "rw")) {
            long t = System.currentTimeMillis();
            long length = raf.length();
            System.out.println("文件长度:" + length);
            long blockNum = length / 1024;
            System.out.println("文件块数:" + blockNum);
            for (long i = 0; i < blockNum; i++) {
                byte[] b1 = new byte[1024];
                raf.seek(0 + i * 1024);
                raf.read(b1, 0, 1024);
                byte[] b2 = encrypt(b1, key.getBytes());
                raf.seek(0 + i * 1024);
                raf.write(b2);
            }
            System.out.print("100M加密耗时:");
            System.out.println(System.currentTimeMillis() - t);
        }
    }

    /**
     * 100M 耗时 1s
     * 11G 耗时272s 4分钟
     * 文件长度:12287384528
     * 文件块数:11999398
     * 100M加密耗时:272853
     * <p>
     * 对称加密迎来崭新的时代
     */
    @Test
    public void test解密100M() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(new File("D:\\Data\\网龙-整理.rar"), "rw")) {
            long t = System.currentTimeMillis();
            long length = raf.length();
            System.out.println("文件长度:" + length);
            long blockNum = length / 1024;
            System.out.println("文件块数:" + blockNum);
            for (long i = 0; i < blockNum; i++) {
                byte[] b1 = new byte[1024];
                raf.seek(0 + i * 1024);
                raf.read(b1, 0, 1024);
                byte[] b2 = decrypt(b1, key.getBytes());
                raf.seek(0 + i * 1024);
                raf.write(b2);
            }
            System.out.print("100M加密耗时:");
            System.out.println(System.currentTimeMillis() - t);
        }
    }

    @Test
    public void testMd5() {

    }

}

