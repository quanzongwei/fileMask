package com.qzw.demo.java.filehide;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.AcceptPendingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * simple encryption algorithm
 * 简单加密算法, 算法简单但是位运算耗时,
 * <p>
 * 虽然我想出来了,但是人家早就想出来了
 * ref: https://en.wikipedia.org/wiki/XOR_cipher
 * 知识这个算法存在一些问题, 已知明文和密文的情况下, 能推测出密码, 如果逐个字节加密的话
 * 同时: 取非操作是不需要的, 因为原来是1保持原bit不变, 变成了0保持原bit不变而已
 * <p>
 * v2: 对文件加密: 使用uuid作为随机数,加上你的秘钥, 就能得到动态秘钥的效果, 随机数32字节, 隐藏在被加密的内容中;
 * 秘钥能够算出他的位置, 我们需要在尾部追加32字节的数据,用来恢复原文数据; 秘钥长度随意,因为有随机数,至少会把秘钥长度扩展到32字节长;
 * 原文长度也随意, 至少会有(1Byte+32字节(随机数原文,或者还是加密吗, 其实可以不用加密32! 理论上已经无法破解了)+32字节(尾部保存了原来的数据)+8字节表示原文长度+1字节(表示是否是文件的尾部))
 * 为了区分不在每一个块中加上是否是文件尾部的判断, 所以这个单做参数,给上层应用自己选择
 * <p>
 * 秘钥取出MD5值, 16字节,主要是担心秘钥太短了,不好将随机数数据随机到文件的各个地方
 * <p>
 * 如果原文长度过短, 为了不让暴露原文长度, 则填充32字节到64字节的数据在尾部之前, 从而隐藏原文的真实长度, 不过好像没有必要啊; 比如OK和NO,都是两个字节, 穷举法没有意义,
 * <p>
 * 同时, 追加一个字节表示是否是文件尾部也没有意义, 所以,只需要是32字节的随机数据就好了
 * <p>
 * 穷举出来的数据你无法确定原文的真实意图. 所以没有必要. 这并不是MD5验证密码的场景, MD5是任何时候得出的密文是一样的, SEA是密文和当前时间有关系,
 * 和uuid全球唯一的ID有关系, 随意他们的使用场景不一样
 * <p>
 * 该算法对原文和秘钥的长度没有要求, 秘钥至少会被扩展为32字节, 因为这是随机数的字节数
 * <p>
 * 算法的时间复杂度和xor是一样的, 我们所做的事情只是吧秘钥动态化了
 * <p>
 * v3: 全文件加密, 写博客吧!!!
 * 最后确定的尾部数据是32字节+8字节=40字节
 * <p>
 * v4:新方案
 * 文件原始长度不加密, 加密时候的随机数不加密; 我们只保证随机数无法被获取, 保证秘钥无论什么方式都无法获取, 比如:
 * 1已知明文的解密操作
 * 2头部加随机数不行, 这个好像又可以了,当时好像是怕随机数被解密; 难道可以改进成, 随机数放在最末尾的形式吗? todo
 * 确实可以, 如果我有一个秘钥,
 * 1. 使用一个秘钥,且不给随机数加密, 知道内容和密文,推出xor后的秘钥, 又因为随机数已知,于是根据xor推出部分秘钥
 * 2. 使用一个秘钥,使用这个秘钥xor随机数加密, 这时候好像也可以, 因为他们不知道我的随机数原文,这种也行,随机数未知--这种方案最简单,
 * 效率最高好的OK非常nice, 你要明白一点,在你复杂的设计之下,一定会有更加简单的设计todo
 * 在考虑一种极端场景: 程序可以被修改, 那么密码直接被记录,或者改了随机数算法(你可以使用MD52的值,原文就不会被推出,但是没必要,能推出密码1,
 * 那他还不如直接记录用户的密码, 一定是程序被改动了),在加上程序的md5校验吗, 那md5校验程序也被修改过呢?所以服务器中下载最新的加密程序,或者md5校验程序,
 * 服务器不可以被破解的,这种情况就能保证数据的安全性了
 * 导致密码被部分推出 没有安全性可言
 * 3. 假设随机数明文和密文都知道, 那么此时可以推出部分秘钥, 如果文本长度很短,则原文可破解
 * 3直接根据字节映射, 穷举攻击,比如A被映射到哪里了
 * <p>
 * 移位操作:
 * 这种设计避免了大量的移位操作,移位操作的时间复杂度从O(n)变成了O(1)
 *
 * @date 2020/1/10
 */
public class SimpleEncryptionAlgorithmV3ReleaseTagRandomHideInFile {


    // measured in bytes
    private static Integer blockSize = 1024;

    /**
     * 你给我多长, 我就返回多长, pass可以重复使用
     *
     * @param originContent 长度和pass的长度相同
     * @param pass
     * @return
     */
    public static byte[] encrypt(byte[] originContent, byte[] pass) {

        byte[] encryptedContent = new byte[originContent.length];
        //取模运算换成,外层拼接pass的模式
        byte[] newPass = new byte[originContent.length];
        Integer passLen = pass.length;
        for (int i = 0; i < newPass.length; i++) {
            // 取余
            newPass[i] = pass[i % passLen];
        }
        for (int i = 0; i < originContent.length; i++) {
            byte b1 = originContent[i];
            byte b2 = newPass[i];

            byte b3 = (byte) (b1 ^ b2);
            encryptedContent[i] = b3;
        }
        return encryptedContent;
    }

    public static byte[] decrypt(byte[] encryptedContent, byte[] pass) {
        byte[] decryptedContent = new byte[encryptedContent.length];
        //取模运算换成,外层拼接pass的模式
        byte[] newPass = new byte[encryptedContent.length];
        Integer passLen = pass.length;
        for (int i = 0; i < newPass.length; i++) {
            // 取余
            newPass[i] = pass[i % passLen];
        }
        for (int i = 0; i < encryptedContent.length; i++) {
            byte b1 = encryptedContent[i];
            byte b2 = newPass[i];

            byte b3 = (byte) (b1 ^ b2);
            decryptedContent[i] = b3;
        }
        return decryptedContent;
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

    /**
     * v3, 耗时: 1.6s ,将近翻了一倍
     */
    @Test
    public void test加密V3() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(new File("D:\\Data\\lottery-原件 - 副本.rar"), "rw")) {
            long t = System.currentTimeMillis();

            long length = raf.length();
            System.out.println("文件长度:" + length);
            long blockNum = length / 1024;
            Long remain = length % 1024;

            Long appendedTextLen = length;
            // 一下处理32字节以及以上的数据
            System.out.println("文件块数:" + blockNum);
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            System.out.println(uuid.length());

            byte[] transformedPassBytes = this.getBytesByUUidBytesAndPassStr(uuid.getBytes(), key);
            for (long i = 0; i < blockNum; i++) {
                byte[] b1 = new byte[1024];
                raf.seek(0 + i * 1024);
                raf.read(b1, 0, 1024);
                byte[] b2 = encrypt(b1, transformedPassBytes);
                raf.seek(0 + i * 1024);
                raf.write(b2);
            }
            // 尾部数据处理
            if (remain > 0) {
                byte[] b3 = new byte[remain.intValue()];
                raf.seek(0 + blockNum * 1024);
                raf.read(b3, 0, remain.intValue());
                byte[] b4 = encrypt(b3, transformedPassBytes);
                raf.seek(0 + blockNum * 1024);
                raf.write(b4);
            }

            // 这些非原文数据无需加密
            String s = RandomStringUtils.randomAscii(32, 64);
            byte[] appendBytes = s.getBytes();
            appendedTextLen = appendedTextLen + appendBytes.length;
            raf.write(appendBytes);
            //追加加密算法尾部数据
            List<Long> list = this.get32LongValue4position(key, appendedTextLen);
            //被交换的加密后的原文字节
            byte[] switchedEncryptTxt = new byte[32];

            for (int i = 0; i < list.size(); i++) {
                Long position = list.get(i);
                raf.seek(position);
                byte[] bt = new byte[1];
                raf.read(bt);
                switchedEncryptTxt[i] = bt[0];

                raf.seek(position);
                // 随机数替换
                raf.write(uuid.getBytes()[i]);
            }
            raf.seek(appendedTextLen);
            raf.write(switchedEncryptTxt);
            //测试byteutil的长度 ,todo 原文长度加密, 原文长度和秘钥相关 8字节, 原文长度不能包含填充的字节数
            raf.write(ByteUtil.longToBytes(length));
            System.out.println("length" + length);
            System.out.print("100M加密耗时:");
            System.out.println(System.currentTimeMillis() - t);
        }
    }

    //uuid和长度暂时未加密
    @Test
    public void test解密V3() throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(new File("D:\\Data\\lottery-原件 - 副本.rar"), "rw")) {
            long t = System.currentTimeMillis();

            long length = raf.length();
            Long algorithmBeginPosition = length - 40;
            byte[] tailBytes = new byte[algorithmBeginPosition.intValue()];
            byte[] switchedEncryptTxt = new byte[32];
            byte[] originTextLen = new byte[8];

            raf.seek(algorithmBeginPosition);
            raf.read(tailBytes);
            System.arraycopy(tailBytes, 0, switchedEncryptTxt, 0, 32);
            System.arraycopy(tailBytes, 32, originTextLen, 0, 8);
            List<Long> list = this.get32LongValue4position(key, algorithmBeginPosition);

            byte[] switchedUuidBytes = new byte[32];
            for (int i = 0; i < list.size(); i++) {
                Long position = list.get(i);
                raf.seek(position);
                byte[] bt = new byte[1];
                raf.read(bt);

                // 组装随机数字节序列
                switchedUuidBytes[i] = bt[0];
                raf.seek(position);

                //将原始的加密后的原文字节恢复
                raf.write(switchedEncryptTxt[i]);
            }
            //删除填充的数据
            System.out.println(ByteUtil.bytesToLong(originTextLen));
            raf.setLength(ByteUtil.bytesToLong(originTextLen));
            byte[] transformedPassBytes = this.getBytesByUUidBytesAndPassStr(switchedUuidBytes, key);

            long originLenLong = ByteUtil.bytesToLong(originTextLen);
            long blockNum = originLenLong / 1024;
            Long remain = originLenLong % 1024;
            for (long i = 0; i < blockNum; i++) {
                byte[] b5 = new byte[1024];
                raf.seek(0 + i * 1024);
                raf.read(b5, 0, 1024);
                byte[] b6 = decrypt(b5, transformedPassBytes);
                raf.seek(0 + i * 1024);
                raf.write(b6);
            }
            // 尾部数据处理
            if (remain > 0) {
                byte[] b7 = new byte[remain.intValue()];
                raf.seek(0 + blockNum * 1024);
                raf.read(b7, 0, remain.intValue());
                byte[] b8 = decrypt(b7, transformedPassBytes);
                raf.seek(0 + blockNum * 1024);
                raf.write(b8);
            }
            System.out.println(System.currentTimeMillis() - t);
        }
    }

    /**
     * key 和appendedText确定, 最后的list不重复,且固定
     * 注意, 一个秘钥+一个文件长度=唯一的位置序列, 此处没有用到随机数, 所以所有长度相同的文件
     * 都用相同的位置选择规则,
     * 32个随机位置, 原文至少被填充到>32字节, 32! 理论上已经无法被破解
     * 32!= 2631308369336935301 /年 67 218 012/GHZ 160 000 000
     */
    private List<Long> get32LongValue4position(String key, Long appendedTextLen) {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            byte[] md5 = MD5Utils.getMd5Bytes(key + i);
            byte[] first = new byte[8];
            byte[] last = new byte[8];
            System.arraycopy(md5, 0, first, 0, 8);
            System.arraycopy(md5, 8, last, 0, 8);
            long f = ByteUtil.bytesToLong(first);
            //不允许负数
            f = Math.abs(f);
            long l = ByteUtil.bytesToLong(last);
            l = Math.abs(l);
            long fremain = f % appendedTextLen;
            dealRepeat(list, fremain, appendedTextLen);

            long lremain = l % appendedTextLen;
            dealRepeat(list, lremain, appendedTextLen);

        }
        // 一共返回32个确定的长整型数据, 且确保不重复
        return list;
    }

    private void dealRepeat(List<Long> list, Long value, Long appendedTextLen) {
        if (!list.contains(value)) {
            list.add(value);
            return;
        }
        //出现重复就往后加1,知道最后, 一定全部都不会重复; 此处不需要考虑value+1变为负数的问题, 因为appendedTextLen一定小于Long.max
        value = (value + 1) % appendedTextLen;
        dealRepeat(list, value, appendedTextLen);
    }

    /**
     * pass length 16字节
     *
     * @param uuidBytes
     * @param pass
     * @return
     */
    public byte[] getBytesByUUidBytesAndPassStr(byte[] uuidBytes, String pass) {

        //是否使用md5, 还有待考证, 问题不大
        byte[] md5Bytes = MD5Utils.getMd5Bytes(pass);
        byte[] newPassBytes = new byte[pass.length() * 2];
        System.arraycopy(md5Bytes, 0, newPassBytes, 0, md5Bytes.length);
        System.arraycopy(md5Bytes, 0, newPassBytes, pass.length(), md5Bytes.length);
        for (int i = 0; i < md5Bytes.length; i++) {
            newPassBytes[i] = (byte) (newPassBytes[i] ^ uuidBytes[i]);
        }
        // 返回变换后的密码字节
        return newPassBytes;
    }

    public static void main(String[] args) {
        System.out.println(Math.floorMod(3, 2));
        System.out.println(Math.floorMod(4, 3));
        System.out.println(Math.floorMod(4, 4));
        System.out.println(4 % 4);
        System.out.println(4 % 3);
    }
    // todo 正确性验证, 使用MD5来校验

}

