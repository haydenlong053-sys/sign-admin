package com.ruoyi.web.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

/**
 * 生成RSA签名
 */
public class GenerateRsaSameSizeAndSign {

    public static void main(String[] args) throws Exception {

        int keySize = 1024;  // 长度

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(keySize);
        KeyPair kp = kpg.generateKeyPair();

        String publicKey = Base64.getEncoder()
                .encodeToString(kp.getPublic().getEncoded());

        String privateKey = Base64.getEncoder()
                .encodeToString(kp.getPrivate().getEncoded());

        System.out.println("========= 新密钥 =========");
        System.out.println("publicKey:");
        System.out.println(publicKey);
        System.out.println();
        System.out.println("privateKey:");
        System.out.println(privateKey);
    }
}