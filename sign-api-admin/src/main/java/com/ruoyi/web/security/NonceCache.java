package com.ruoyi.web.security;

//import com.app.common.util.RedisUtil;
import com.ruoyi.web.core.config.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 10:42
 */
@Component
public class NonceCache {

    @Autowired
    private RedisCache redisCache;

    public boolean checkAndAdd(String nonce, long expireSeconds) {
        String cacheKey = "NONCE:" + nonce;
        Object d = redisCache.getCacheObject(cacheKey);
        if (d!=null) {
            return false;
        }
        redisCache.setCacheObject(cacheKey, "1", (int) expireSeconds,TimeUnit.SECONDS);
        return true;
    }
}
