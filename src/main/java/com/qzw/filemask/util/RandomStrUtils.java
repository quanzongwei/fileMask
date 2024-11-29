package com.qzw.filemask.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * @author quanzognwei
 * @date 2024/11/29 19:13
 */
public class RandomStrUtils {
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成指定长度的base64随机字符串
     */
    public static String generateBase64RandomString(int length) {
        // Calculate the number of bytes needed to get at least `length` Base64 characters
        int numBytes = (int) Math.ceil(length * 3 / 4.0);
        // Generate random bytes
        byte[] randomBytes = new byte[numBytes];
        RANDOM.nextBytes(randomBytes);
        // Encode bytes to Base64
        String base64Str = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        // Return the first `length` characters of the Base64 string
        return base64Str.substring(0, length);
    }

    /**
     * 基于uuid生成base64字符串
     * 目的：缩短字符长度，32 → 23
     */
    public static String generateUUIDBase64String() {
        // 假设我们有一个 UUID，并将其转换为字节数组
        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = new byte[16];
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            uuidBytes[i] = (byte) (mostSigBits >>> (8 * (7 - i)));
            uuidBytes[8 + i] = (byte) (leastSigBits >>> (8 * (7 - i)));
        }
        // 使用 Base64 URL 安全的编码器，并且不带填充字符
        return Base64.getUrlEncoder().withoutPadding().encodeToString(uuidBytes);
    }
}
