package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * BSC 提现日志 bsc_withdrawal_log
 */
@Getter
@Setter
@Accessors(chain = true)
public class BscWithdrawalLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 标记删除，0:未删除 / 1:已删除 */
    private Integer flag;

    /** 来源 1:链桥 2:IM */
    private Integer originType;

    /** 来源方系统的用户ID */
    private String userId;

    /** 币种 1:WX 2:WEBX */
    private Integer coinId;

    /** 出账时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date billingTime;

    private String fromAddress;

    private String toAddress;

    /** 链上交易 hash */
    private String hash;

    /** 提现状态 0:发起提现 1:正在提现 2:提现成功 3:提现失败 */
    private Integer status;

    /** 签名状态 0:未结束 1:签名完成 */
    private Integer signFinished;

    /** 系统内部订单号 */
    private String orderNumber;

    /** 实际到账金额 */
    private BigDecimal amount;

    /** 0=正常出账 1=闪兑出账 */
    private Integer redemption;

    /** 是否通过了大额审核 */
    private Integer largeAmountPassed;

    /** 大额状态 0:未判断 1:是大额订单 2:不是大额订单 */
    private Integer isLargeAmount;

    private BigInteger deadline;

    private Integer signProgressOne;

    private Integer signProgressTwo;

    private Integer signProgressThree;

    private Integer signProgressFour;

}
