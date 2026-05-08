package com.ruoyi.system.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.req.BscWithdrawalSignSubmit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * <p>
 * 提现签名记录
 * </p>
 *
 * @author ll
 * @since 2026-04-15
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class BscWithdrawalSign implements Serializable {

    private static final long serialVersionUID = 1L;
    //"ID")
    private Integer id;

    //"创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    //"更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private String createBy;
    private String updateBy;

    //"标记删除，0 / 1")
    private Integer flag;

    //"提现申请记录ID，关联 bsc_withdrawal_log.id")
    private Long withdrawLogId;

    //"签名人地址")
    private String signerAddress;

    //"签名摘要")
    private String signDigest;

    //"签名结果")
    private String signature;

    //"签名状态 0:待签 1:签名中 2:签名成功 3:签名失败")
    private Integer signStatus;

    //"签名步骤 1:第一审核 2:第二审核 3:第三审核")
    private Integer signStep;

    //"签名服务器标识")
    private String signServer;

    //"备注")
    private String remark;

    //"签名时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signTime;

    //"合约订单号")
    private String orderId;

    //"签名过期时间戳")
    private BigInteger deadline;

    //"用户nonce")
    private BigInteger userNonce;

    //"业务来源标识")
    private BigInteger bizId;

    //"签名失败原因")
    private String failReason;

    public BscWithdrawalSign(BscWithdrawalLog withdrawalLog, BscWithdrawalSignSubmit withdrawalAuditReq,Integer signStep,String signServer ) {
        this.createTime =LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.flag = 0;
        this.withdrawLogId = withdrawalLog.getId();
        this.signerAddress = withdrawalAuditReq.getSignerAddress();
        this.signature = withdrawalAuditReq.getSignature();
        this.signStep = signStep;
        this.signServer = signServer;
        this.signTime = LocalDateTime.now();
        this.orderId = withdrawalLog.getOrderNumber();
        this.deadline = withdrawalLog.getDeadline();
        this.bizId = BigInteger.valueOf(withdrawalLog.getCoinId());
    }
}