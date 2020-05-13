package com.qzw.demo;

import org.junit.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Base64;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    public static void main(String[] args) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("D:\\360极速浏览器下载\\member_coupon_template.xls", "rw");

        Long length = raf.length();
        byte[] data = new byte[length.intValue()];

        raf.read(data);
        System.out.println(length
        );

        String s = Base64.getEncoder().encodeToString(data);
        System.out.println(s);
    }
}
