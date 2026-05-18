package com.ruoyi.common.req;

import java.io.Serializable;

/**
 * RSA 开放接口请求中的签名块（与 {@link com.ruoyi.web.security.RsaSignAspect} 配套）。
 */
public class SignReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private String appId;
    private String nonce;
    private Long timestamp;
    private String sign;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
