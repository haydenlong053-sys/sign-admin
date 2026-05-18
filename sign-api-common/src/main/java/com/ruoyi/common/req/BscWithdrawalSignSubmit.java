package com.ruoyi.common.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 大额提现四号签提交（占位接口入参）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BscWithdrawalSignSubmit implements Serializable {

    private static final long serialVersionUID = 1L;
    //申请记录ID
    private Long withdrawalLogId;

    //"是否通过 1:通过 0:驳回")
    private Integer approved;

    //"备注")
    private String remark;

    //"签名人地址")
    private String signerAddress;

    //"签名摘要")
    private String signDigest;

    //"签名结果")
    private String signature;

    //"审核失败原因")
    private String failReason;
}
