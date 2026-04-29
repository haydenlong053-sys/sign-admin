package com.ruoyi.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 9:43
 */
@Component
public class KeyManager {

    @Autowired
    private ApiSecurityProperties apiSecurityProperties;

    public String getPublicKey(String appId) {
        if("im".equals(appId)){
            return apiSecurityProperties.getIm().getPublicKey();
        }
        return "";
    }
}
