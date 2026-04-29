package com.ruoyi.web.security;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author hayden
 */


@Configuration
@ConfigurationProperties(prefix = "api.security")
public class ApiSecurityProperties {

    private KeyPairConfig im;

    private List<String> allowedIps;

    private Boolean ipEnable;

    public KeyPairConfig getIm() {
        return im;
    }

    public void setIm(KeyPairConfig im) {
        this.im = im;
    }

    public List<String> getAllowedIps() {
        return allowedIps;
    }

    public void setAllowedIps(List<String> allowedIps) {
        this.allowedIps = allowedIps;
    }

    public Boolean getIpEnable() {
        return ipEnable;
    }

    public void setIpEnable(Boolean ipEnable) {
        this.ipEnable = ipEnable;
    }

    public static class KeyPairConfig {
        private String url;
        private String publicKey;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
    }
}