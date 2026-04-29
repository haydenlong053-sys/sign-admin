package com.ruoyi.web.security;

import java.lang.annotation.*;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 9:41
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RsaSignVerify {
    /**
     * 是否校验时间戳
     */
    boolean checkTimestamp() default true;

    /**
     * 是否校验随机串 nonce
     */
    boolean checkNonce() default true;

    /**
     * 时间戳有效期（秒）
     */
    long expireSeconds() default 300; // 默认5分钟内有效

    /**
     * 是否启用ip校验,默认不开启
     */
    boolean checkIp() default false;

    /**
     * 是否把业务参数(data)纳入签名：有data 要 true；没data 要 false
     * @return
     */
    boolean includeData() default false;

    /**
     * 业务字段名，默认就是 data
     * @return
     */
    String dataField() default "data";
}