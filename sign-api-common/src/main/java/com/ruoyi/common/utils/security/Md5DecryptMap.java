package com.ruoyi.common.utils.security;

import java.util.HashMap;
import java.util.Map;

/**
 * MD5 密文反查明文：预计算 000001～999999 的 MD5，用于根据密文查询明文
 *
 * @author HayDen
 */
public class Md5DecryptMap {

    /** MD5密文(32位小写hex) -> 明文(6位数字串 000001~999999) */
    private static final Map<String, String> MD5_TO_PLAIN = new HashMap<>(1000000);

    static {
        for (int i = 1; i <= 999999; i++) {
            String plain = String.format("%06d", i);
            String md5 = Md5Utils.encrypt(plain);
            MD5_TO_PLAIN.put(md5, plain);
        }
    }

    /**
     * 根据 MD5 密文查询明文
     *
     * @param md5Encrypted 加密后的字符串（32位，会转小写再查）
     * @return 明文，范围 000001～999999；未命中返回 null
     */
    public static String getPlaintext(String md5Encrypted) {
        if (md5Encrypted == null || md5Encrypted.isEmpty()) {
            return null;
        }
        String key = md5Encrypted.trim().toLowerCase();
        if (key.length() != 32) {
            return null;
        }
        return MD5_TO_PLAIN.get(key);
    }

    /**
     * 返回预计算表大小（应为 999999）
     */
    public static int size() {
        return MD5_TO_PLAIN.size();
    }
}
