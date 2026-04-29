package com.ruoyi.web.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ruoyi.common.utils.uuid.UUID;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地模拟 IM 端生成签名请求体：
 * 构造 data → 生成 signContent → 私钥签名 → 输出完整请求 JSON
 * 用于测试服务端验签逻辑。
 */
public class ImSignDataMainTest {

    private static final ObjectMapper STABLE_MAPPER = new ObjectMapper()
            .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static void main(String[] args) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();

        Map<String, Object> a = new LinkedHashMap<>();
        a.put("type", 126684);
        a.put("code", "VIP-ABC-41");
        a.put("day", 100);
        data.add(a);

        Map<String, Object> b = new LinkedHashMap<>();
        b.put("idNo", 10002);
        b.put("type", 126681);
        b.put("code", "VIP-ABC-31");
        b.put("day", 190);
        data.add(b);

        Map<String, Object> c = new LinkedHashMap<>();
        c.put("idNo", 10002);
        c.put("type", 126681);
        c.put("code", "VIP-ABC-42");
        c.put("day", 190);
        data.add(c);

        Map<String, Object> d = new LinkedHashMap<>();
        d.put("idNo", 10002);
        d.put("type", 126681);
        d.put("code", "VIP-ABC-43");
        d.put("day", 190);
        data.add(d);

        Map<String, Object> e = new LinkedHashMap<>();
        e.put("type", 126681);
        e.put("code", "VIP-ABC-45");
        e.put("day", 190);
        data.add(e);

        // sign 的三要素
        String appId = "im";
        String nonce = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.currentTimeMillis() / 1000;

        // 把 data 变成“稳定 JSON 字符串”参与签名
        String dataJson = STABLE_MAPPER.writeValueAsString(data);

        // 参与签名的参数（注意：这里的 data 放 dataJson 字符串）
        Map<String, Object> signParams = new LinkedHashMap<>();
        signParams.put("appId", appId);
        signParams.put("nonce", nonce);
        signParams.put("timestamp", timestamp);
        signParams.put("data", dataJson);
        // 客户端私钥
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL8f5eKXfg/6PcXhYeOdqA4naCgoVbM8uzV6Lk8JVRH5bFzygfAUXGCHvzfYOPiZgIqtBpUh5M3SqPIT4Y9l0zlEvxGY+QruqOILtMmTh4c/bs7LKzqoqO02UpN+cL3t50hH6cXoFzkCjmZLibsv3Le0RisoMTzwMf6DHxbz6dyjAgMBAAECgYBDe5vL2fmk4pKVH/NUTwJbnQZlx3gbBUKEq1NWjNEAu7TLTMUcY9QKMWQO78WN4YVFcbp1RbZoBg2pC+QEoctmPOy6Jw/7gpKNVpp+0yDWKtoSwjmD0iyfLrasdp8S70w8FXopGlFc+/E3Wg255YF7CJPipF3R5QbNppr1ruTEoQJBAN/d6TaqdOqQJDljULcO6BTuMuH9h/Z+D5qpzxmGCW7Cxx64X06plbUxN0SgGN/ZMDVdRbnN1d0pz4OTpWROjq0CQQDajtvJFOWNst+eGi9iYfM/vajUSdHlt3DBKiuTkF5mSC3FPwvVGPJSj7pMHIZagixyIf2LxEpStBMPNAq+ABKPAkEAnxoGsixI9Z/FruePqNo2RRHZJdXa+Gs1SdVfc6NYNtjFTWb5jV1nIaQ9xE8H/qdL5NFKSbgpQ4qAieG4DObr7QJADZs3M52JbPjl0wHuUUGn0yALkqvF1XdGHcByVg120wdLUixKoEmdTGaGQpbEBn50JebX0gu2VM/BIAEtaga3IQJALGQT5PI8pTDMNTiKr5XBmeM8dZWlWc1CXKHv2K8bxW6O5WmITDLNShbeZQu8YhdtSy1yM6fYj/xWUgeULSP+yw==";

        String signContent = ImToLqApiSignUtils.buildSignContent(signParams);
        String sign = ImToLqApiSignUtils.sign(signContent, privateKey);

        // 拼“最终请求体”：sign 对象 + data 数组（请求体 data 仍然发数组）
        Map<String, Object> signObj = new LinkedHashMap<>();
        signObj.put("appId", appId);
        signObj.put("nonce", nonce);
        signObj.put("timestamp", timestamp);
        signObj.put("sign", sign);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sign", signObj);
        body.put("data", data);

        System.out.println("加密后的body体：" + STABLE_MAPPER.writeValueAsString(body));
    }

}