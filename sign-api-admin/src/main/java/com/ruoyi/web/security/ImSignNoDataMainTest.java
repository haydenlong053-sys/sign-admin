package com.ruoyi.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.uuid.UUID;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 本地模拟 IM 端生成仅签名三要素的请求体：
 * 构造 appId/nonce/timestamp → 生成 signContent → 私钥签名 → 输出请求 JSON
 * 用于测试 includeData=false 接口的验签逻辑。
 */

public class ImSignNoDataMainTest {

    public static void main(String[] args) throws Exception {

        String appId = "im";
        String nonce = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis() / 1000;

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("appId", appId);
        params.put("nonce", nonce);
        params.put("timestamp", timestamp);

        String signContent = ImToLqApiSignUtils.buildSignContent(params);

        // IM 私钥
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL8f5eKXfg/6PcXhYeOdqA4naCgoVbM8uzV6Lk8JVRH5bFzygfAUXGCHvzfYOPiZgIqtBpUh5M3SqPIT4Y9l0zlEvxGY+QruqOILtMmTh4c/bs7LKzqoqO02UpN+cL3t50hH6cXoFzkCjmZLibsv3Le0RisoMTzwMf6DHxbz6dyjAgMBAAECgYBDe5vL2fmk4pKVH/NUTwJbnQZlx3gbBUKEq1NWjNEAu7TLTMUcY9QKMWQO78WN4YVFcbp1RbZoBg2pC+QEoctmPOy6Jw/7gpKNVpp+0yDWKtoSwjmD0iyfLrasdp8S70w8FXopGlFc+/E3Wg255YF7CJPipF3R5QbNppr1ruTEoQJBAN/d6TaqdOqQJDljULcO6BTuMuH9h/Z+D5qpzxmGCW7Cxx64X06plbUxN0SgGN/ZMDVdRbnN1d0pz4OTpWROjq0CQQDajtvJFOWNst+eGi9iYfM/vajUSdHlt3DBKiuTkF5mSC3FPwvVGPJSj7pMHIZagixyIf2LxEpStBMPNAq+ABKPAkEAnxoGsixI9Z/FruePqNo2RRHZJdXa+Gs1SdVfc6NYNtjFTWb5jV1nIaQ9xE8H/qdL5NFKSbgpQ4qAieG4DObr7QJADZs3M52JbPjl0wHuUUGn0yALkqvF1XdGHcByVg120wdLUixKoEmdTGaGQpbEBn50JebX0gu2VM/BIAEtaga3IQJALGQT5PI8pTDMNTiKr5XBmeM8dZWlWc1CXKHv2K8bxW6O5WmITDLNShbeZQu8YhdtSy1yM6fYj/xWUgeULSP+yw==";
        String sign = ImToLqApiSignUtils.sign(signContent, privateKey);

        Map<String, Object> signObj = new LinkedHashMap<>();
        signObj.put("appId", appId);
        signObj.put("nonce", nonce);
        signObj.put("timestamp", timestamp);
        signObj.put("sign", sign);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sign", signObj);

        System.out.println("signContent=" + signContent);
        System.out.println("请求体JSON=" +
                new ObjectMapper().writeValueAsString(body));
    }

}
