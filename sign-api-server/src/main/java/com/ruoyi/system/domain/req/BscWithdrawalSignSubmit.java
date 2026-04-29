package com.ruoyi.system.domain.req;

import java.io.Serializable;

/**
 * 大额提现四号签提交（占位接口入参）
 */
public class BscWithdrawalSignSubmit implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /** 前端签名结果（hex 等） */
    private String signature;
    /** 签名所用地址 */
    private String signerAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignerAddress() {
        return signerAddress;
    }

    public void setSignerAddress(String signerAddress) {
        this.signerAddress = signerAddress;
    }
}
