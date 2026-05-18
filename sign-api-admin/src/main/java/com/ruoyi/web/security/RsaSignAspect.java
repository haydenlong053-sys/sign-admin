package com.ruoyi.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 9:44
 */
@Aspect
@Component
public class RsaSignAspect {

    @Autowired
    private NonceCache nonceCache;

    @Autowired
    private KeyManager keyManager;

    @Autowired
    private ApiSecurityProperties apiSecurityProperties;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** 稳定 JSON：字段排序 + map key 排序 + 忽略 null，避免两端序列化差异导致验签失败 */
    private static final ObjectMapper STABLE_MAPPER = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
            .configure(com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);

    private String stableJson(Object obj) {
        try {
            return STABLE_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new SecurityException("data 序列化失败，无法验签");
        }
    }

    @Pointcut("@annotation(com.ruoyi.web.security.RsaSignVerify)")
    public void verifyPoint() {
    }

    /**
     *  提取请求中的签名三要素（appId、nonce、timestamp、sign）
     *  根据注解配置决定是否将 data 参与签名验证，@RsaSignVerify(includeData = true)
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("verifyPoint()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RsaSignVerify annotation = method.getAnnotation(RsaSignVerify.class);
        if (annotation == null) return joinPoint.proceed();

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return joinPoint.proceed();
        HttpServletRequest request = attrs.getRequest();

        // IP 校验
        if (Boolean.TRUE.equals(apiSecurityProperties.getIpEnable()) && annotation.checkIp()) {
            validateIp(request, apiSecurityProperties.getAllowedIps());
        }

        String appId = null;
        String nonce = null;
        Long timestamp = null;
        String sign = null;
        Object dataObj = null;

        // 从入参取（优先，避免 body 被消费）
        Object[] args = joinPoint.getArgs();
        if (args != null) {
            for (Object arg : args) {
                if (arg == null) continue;

                // 无额外参数接口
                if (arg instanceof com.ruoyi.common.req.SignReq) {
                    com.ruoyi.common.req.SignReq s = (com.ruoyi.common.req.SignReq) arg;
                    appId = s.getAppId();
                    nonce = s.getNonce();
                    timestamp = s.getTimestamp();
                    sign = s.getSign();
                    break;
                }

                // 有额外参数接口
                Object signObj = invokeGetter(arg, "getSign");
                if (signObj instanceof com.ruoyi.common.req.SignReq) {
                    com.ruoyi.common.req.SignReq s = (com.ruoyi.common.req.SignReq) signObj;
                    appId = s.getAppId();
                    nonce = s.getNonce();
                    timestamp = s.getTimestamp();
                    sign = s.getSign();

                    if (annotation.includeData()) {
                        dataObj = invokeGetter(arg, "getData"); // 默认字段 data
                    }
                    break;
                }
            }
        }

        // 入参拿不到再读 body（兼容兜底）
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(sign)) {
            byte[] bodyBytes = StreamUtils.copyToByteArray(request.getInputStream());
            String body = new String(bodyBytes, StandardCharsets.UTF_8);
            if (StringUtils.isBlank(body)) throw new SecurityException("请求体为空，无法验签");

            Map<String, Object> root = MAPPER.readValue(body, Map.class);
            Object signNode = root.get("sign");

            if (signNode instanceof Map) {
                Map<String, Object> signMap = (Map<String, Object>) signNode;
                appId = (String) signMap.get("appId");
                nonce = (String) signMap.get("nonce");
                sign = (String) signMap.get("sign");
                timestamp = signMap.get("timestamp") == null ? null : Long.parseLong(signMap.get("timestamp").toString());
            } else {
                // 兼容老格式
                appId = (String) root.get("appId");
                nonce = (String) root.get("nonce");
                sign = (String) root.get("sign");
                timestamp = root.get("timestamp") == null ? null : Long.parseLong(root.get("timestamp").toString());
            }

            if (annotation.includeData() && dataObj == null) {
                dataObj = root.get(annotation.dataField()); // 默认 data
            }
        }

        if (StringUtils.isBlank(appId) || StringUtils.isBlank(sign)) {
            throw new SecurityException("缺少 appId 或 sign");
        }

        // 时间戳校验
        if (annotation.checkTimestamp()) {
            if (timestamp == null) throw new SecurityException("缺少 timestamp");
            long now = Instant.now().getEpochSecond();
            if (Math.abs(now - timestamp) > annotation.expireSeconds()) {
                throw new SecurityException("请求已过期或时间戳不合法");
            }
        }

        // nonce校验
        if (annotation.checkNonce()) {
            if (StringUtils.isBlank(nonce)) throw new SecurityException("缺少 nonce");
            boolean first = nonceCache.checkAndAdd(nonce, annotation.expireSeconds());
            if (!first) throw new SecurityException("重复请求，nonce 已使用");
        }

        // 组装签名参数
        Map<String, Object> signParams = new TreeMap<>();
        signParams.put("appId", appId);
        signParams.put("nonce", nonce);
        signParams.put("timestamp", timestamp);

        if (annotation.includeData()) {
            if (dataObj == null) throw new SecurityException("缺少 data，无法验签");
            signParams.put(annotation.dataField(), stableJson(dataObj));
        }

        String signContent = buildSignContent(signParams);

        String pubKeyStr = keyManager.getPublicKey(appId);
        if (StringUtils.isBlank(pubKeyStr)) throw new SecurityException("无效 appId");

        PublicKey pubKey = RsaSignUtils.loadPublicKey(pubKeyStr);
        if (!RsaSignUtils.verify(signContent, sign, pubKey)) {
            throw new SecurityException("签名验证失败");
        }

        return joinPoint.proceed();
    }

    private Object invokeGetter(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            return m.invoke(target);
        } catch (Exception ignore) {
            return null;
        }
    }


    private String buildSignContent(Map<String, Object> params) {
        Map<String, Object> sorted = new TreeMap<>();
        for (Map.Entry<String, Object> e : params.entrySet()) {
            if (!"sign".equals(e.getKey()) && e.getValue() != null) {
                sorted.put(e.getKey(), e.getValue());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> e : sorted.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(e.getKey()).append("=").append(e.getValue());
        }
        return sb.toString();
    }

    /**
     * ip白名单校验
     */
    private void validateIp(HttpServletRequest request, List<String> allowedIps) {
        String origin = IpUtils.getIpAddr(request);
        if (origin == null || allowedIps == null || allowedIps.isEmpty()) {
            throw new SecurityException("请求来源ip非法");
        }
        String[] arrIp = origin.split(",");
        boolean allowed = false;
        for (String ip : arrIp) {
            ip = ip.trim();
            allowed = allowedIps.stream().anyMatch(ip::startsWith);
            if (allowed) {
                //  匹配到ip
                break;
            }
        }
        if (!allowed) {
            throw new SecurityException("请求来源ip非法: " + origin);
        }
    }
}