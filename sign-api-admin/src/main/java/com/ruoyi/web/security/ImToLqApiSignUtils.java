package com.ruoyi.web.security;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class ImToLqApiSignUtils {

    /**
     * 构造待签名字符串（按 key 排序）
     */
    public static String buildSignContent(Map<String, Object> params) {
        Map<String, Object> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : sorted.entrySet()) {
            if ("sign".equals(e.getKey()) || e.getValue() == null) continue;
            if (sb.length() > 0) sb.append("&");
            sb.append(e.getKey()).append("=").append(e.getValue());
        }
        return sb.toString();
    }

    /**
     * 使用私钥签名（LQ → IM 返回时用）
     */
    public static String sign(String data, String base64PrivateKey) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(signature.sign());
    }

}