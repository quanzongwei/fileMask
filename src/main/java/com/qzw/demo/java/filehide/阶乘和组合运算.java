package com.qzw.demo.java.filehide;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author BG388892
 * @date 2020/1/15
 */
public class 阶乘和组合运算 {

    // 计算C(256*2)
    // 8 个值 C(256,2)*C(254,2)...一共8个 36位
    @Test
    public void test() {
        // 256! =207位, 穷举已经是不可能了
        System.out.println(getFactorial(BigInteger.valueOf(256)).toString().length());
        int n = 2;
        int i = 254;
        BigInteger result = new BigInteger("1");
        int time = 1;
        System.out.println(result);
        while (i >= 2) {
            if (time > 8) {
                break;
            }
            // n!
            result = result.multiply(getCNMValue(BigInteger.valueOf(i), BigInteger.valueOf(2)));
            //
            System.out.println(result.toString().length());
            i = i - 2;

            time++;
        }
        System.out.println(result.toString().length());

    }

    // C(n,m) ,m 在n的中间, 只是最大的
    @Test
    public void testC52() {
        BigInteger cnmValue = getCNMValue(BigInteger.valueOf(256L), BigInteger.valueOf(2L));
        BigInteger cnmValue2 = getCNMValue(BigInteger.valueOf(256L), BigInteger.valueOf(3L));
        BigInteger cnmValue3 = getCNMValue(BigInteger.valueOf(256L), BigInteger.valueOf(10L));
        // 10078751602022313874633200
        BigInteger cnmValue4 = getCNMValue(BigInteger.valueOf(256L), BigInteger.valueOf(16L));
        System.out.println(cnmValue);
        System.out.println(cnmValue2);
        System.out.println(cnmValue3);
        System.out.println(cnmValue4);
        System.out.println(getCNMValue(BigInteger.valueOf(256L), BigInteger.valueOf(128L)));
        System.out.println(getCNMValue(BigInteger.valueOf(256L), BigInteger.valueOf(127L)));
        System.out.println(getCNMValue(BigInteger.valueOf(256L), BigInteger.valueOf(129L)));

    }

    @Test
    public void test2byteMapping() {
        BigInteger cnmValue = getCNMValue(BigInteger.valueOf(256L), BigInteger.valueOf(2L)).multiply(getCNMValue(BigInteger.valueOf(254L), BigInteger.valueOf(2L))).multiply(getCNMValue(BigInteger.valueOf(252L), BigInteger.valueOf(2L)));
        System.out.println(cnmValue);

    }

    //阶乘运算
    BigInteger getFactorial(BigInteger i) {
        if (i.equals(BigInteger.valueOf(1))) {
            return BigInteger.valueOf(1);
        }
        return i.multiply(getFactorial(i.subtract(BigInteger.valueOf(1))));
    }


    // 组合运算 C(n,m) n是底数
    BigInteger getCNMValue(BigInteger n, BigInteger m) {
        return getFactorial(n).divide(getFactorial(n.subtract(m)).multiply(getFactorial(m)));
    }


    @Test
    public void testRefrence() {
        BigInteger bigInteger = BigInteger.valueOf(10L);
        System.out.println(bigInteger.toString());
        System.out.println(bigInteger.multiply(bigInteger.subtract(BigInteger.valueOf(5))));

        System.out.println(bigInteger);
        // 原来的值不会被改变
        //10
        //50
        //10


    }

    @Test
    public void testBigDecimal() {
        BigDecimal bigInteger = BigDecimal.valueOf(10L);
        System.out.println(bigInteger.toString());
        System.out.println(bigInteger.multiply(bigInteger.subtract(BigDecimal.valueOf(5))));

        System.out.println(bigInteger);
        // 原来的值不会被改变, he bigInteger保持一样
        //10
        //50
        //10


    }

}
