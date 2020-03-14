package com.qzw.demo.java.filehide;

import java.nio.ByteBuffer;

/**
 * @author BG388892
 * @date 2020/1/16
 */
public class 题目测试 {
    /**
     * 0111 127
     * 1000 -128
     * 1111 -1
     * <p>
     * 负二进制:-1111 取反 0000 +1 =1 就是负数的绝对值
     * 127+1=-128
     */
    public static void main2(String args[]) {
        byte num = 127;
        num += 1;
        System.out.println(num);
        num += 1;
        System.out.println(num);
        System.out.println((byte) 0x7E);
    }

    // break 一致往下, 直到遇到break
    public static void main3(String args[]) {
        char c = 'A';
        int num = 10;
        switch (c) {
            case 'B':
                num++;
            case 'A':
                num++;
            case 'Y':
                num++;
                break;
            default:
                num--;
        }
        System.out.println(num);
    }

    // 考点 && 有截断的功能, 所以不会算到1/0
    public static void main4(String args[]) {
        boolean flag = 10 % 2 == 1 && 10 / 3 == 0 && 1 / 0 == 0;
        System.out.println(flag ? "mldn" : "yootk");
    }

    // 考点 0%3==0
    public static void main(String args[]) {
        int sum = 0;
        for (int x = 0; x < 10; x++) {
            sum += x;
            if (x % 3 == 0) {
                break;
            }
        }
        System.out.println(sum);
    }
}
