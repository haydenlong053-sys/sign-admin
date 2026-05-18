package com.ruoyi.server.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * BSC提现对账记录对象 withdraw_reconcile_log
 * 
 * @author ruoyi
 */
public class WithdrawReconcileLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 标记删除，0:未删除 / 1:已删除 */
    private Integer flag;

    /** 【业务】系统内部订单号 */
    @Excel(name = "业务订单号")
    private String bizOrderNumber;

    /** 【业务】业务订单是否存在：0未检查 1存在 2不存在 */
    @Excel(name = "业务订单状态", readConverterExp = "0=未检查,1=存在,2=不存在")
    private Integer bizOrderExists;

    /** 【业务】来源 1:链桥 2:IM */
    @Excel(name = "来源", readConverterExp = "1=链桥,2=IM")
    private Integer bizOriginType;

    /** 【业务】来源方系统的用户ID */
    @Excel(name = "业务用户ID")
    private String bizUserId;

    /** 【业务】币种 1:WX 2:WEBX 3:链桥老系统 */
    @Excel(name = "币种", readConverterExp = "1=WX,2=WEBX,3=链桥老系统")
    private Integer bizCoinId;

    /** 【业务】提现收款地址 */
    @Excel(name = "业务收款地址")
    private String bizToAddress;

    /** 【业务】提现状态 0:发起提现 1:正在提现 2:提现成功 3:提现失败 */
    @Excel(name = "业务状态", readConverterExp = "0=发起提现,1=正在提现,2=提现成功,3=提现失败")
    private Integer bizStatus;

    /** 【业务】实际到账金额(扣除手续费) */
    @Excel(name = "业务金额")
    private BigDecimal bizAmount;

    /** 【业务】出账类型：0正常出账 1闪兑出账 */
    @Excel(name = "业务出账类型", readConverterExp = "0=正常出账,1=闪兑出账")
    private Integer bizRedemption;

    /** 【业务】业务系统交易Hash（关联用） */
    @Excel(name = "业务交易Hash")
    private String bizHash;

    /** 【链上】提现订单号 */
    @Excel(name = "链上订单号")
    private String chainOrderId;

    /** 【链上】收款地址 */
    @Excel(name = "链上收款地址")
    private String chainUserAddress;

    /** 【链上】提现金额 */
    @Excel(name = "链上金额")
    private BigDecimal chainAmount;

    /** 【链上】业务类型：0正常提币 1闪兑提币 */
    @Excel(name = "链上业务类型", readConverterExp = "0=正常提币,1=闪兑提币")
    private Integer chainRedemption;

    /** 【链上】交易Hash */
    @Excel(name = "链上交易Hash")
    private String chainTxHash;

    /** 【链上】区块号 */
    @Excel(name = "区块号")
    private Long chainBlockNumber;

    /** 【链上】日志索引 */
    @Excel(name = "日志索引")
    private Long chainLogIndex;

    /** 【链上】执行地址 */
    @Excel(name = "执行地址")
    private String chainExecutor;

    /** 【链上】事件时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "链上事件时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date chainTimestamp;

    /** 【链上】合约类型：ODIC/U */
    @Excel(name = "合约类型")
    private String chainContractType;

    /** 【链上】合约地址 */
    @Excel(name = "合约地址")
    private String chainContractAddress;

    /** 【对账】提现状态是否一致：0否 1是 */
    @Excel(name = "状态一致", readConverterExp = "0=否,1=是")
    private Integer reconcileStatusMatch;

    /** 【对账】金额是否一致：0否 1是 */
    @Excel(name = "金额一致", readConverterExp = "0=否,1=是")
    private Integer reconcileAmountMatch;

    /** 【对账】用户地址是否一致：0否 1是 */
    @Excel(name = "用户一致", readConverterExp = "0=否,1=是")
    private Integer reconcileUserMatch;

    /** 【对账】交易Hash是否一致：0否 1是 */
    @Excel(name = "Hash一致", readConverterExp = "0=否,1=是")
    private Integer reconcileHashMatch;

    /** 【对账】类型是否一致：0否 1是 */
    @Excel(name = "类型一致", readConverterExp = "0=否,1=是")
    private Integer reconcileRedemptionMatch;

    /** 【对账】状态：0待对账 1对账成功 2对账异常 */
    @Excel(name = "对账状态", readConverterExp = "0=待对账,1=对账成功,2=对账异常")
    private Integer reconcileStatus;

    /** 【对账】结果类型：1完全一致 2业务有链上无 3链上有业务无 4金额不一致 5Hash不一致 6用户不一致 7类型不一致 */
    @Excel(name = "对账结果", readConverterExp = "1=完全一致,2=业务有链上无,3=链上有业务无,4=金额不一致,5=Hash不一致,6=用户不一致,7=类型不一致")
    private Integer reconcileType;

    /** 【对账】备注 */
    @Excel(name = "对账备注")
    private String reconcileRemark;

    /** 【对账】时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "对账时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reconcileTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setFlag(Integer flag) 
    {
        this.flag = flag;
    }

    public Integer getFlag() 
    {
        return flag;
    }

    public void setBizOrderNumber(String bizOrderNumber) 
    {
        this.bizOrderNumber = bizOrderNumber;
    }

    public String getBizOrderNumber() 
    {
        return bizOrderNumber;
    }

    public void setBizOrderExists(Integer bizOrderExists) 
    {
        this.bizOrderExists = bizOrderExists;
    }

    public Integer getBizOrderExists() 
    {
        return bizOrderExists;
    }

    public void setBizOriginType(Integer bizOriginType) 
    {
        this.bizOriginType = bizOriginType;
    }

    public Integer getBizOriginType() 
    {
        return bizOriginType;
    }

    public void setBizUserId(String bizUserId) 
    {
        this.bizUserId = bizUserId;
    }

    public String getBizUserId() 
    {
        return bizUserId;
    }

    public void setBizCoinId(Integer bizCoinId) 
    {
        this.bizCoinId = bizCoinId;
    }

    public Integer getBizCoinId() 
    {
        return bizCoinId;
    }

    public void setBizToAddress(String bizToAddress) 
    {
        this.bizToAddress = bizToAddress;
    }

    public String getBizToAddress() 
    {
        return bizToAddress;
    }

    public void setBizStatus(Integer bizStatus) 
    {
        this.bizStatus = bizStatus;
    }

    public Integer getBizStatus() 
    {
        return bizStatus;
    }

    public void setBizAmount(BigDecimal bizAmount) 
    {
        this.bizAmount = bizAmount;
    }

    public BigDecimal getBizAmount() 
    {
        return bizAmount;
    }

    public void setBizRedemption(Integer bizRedemption) 
    {
        this.bizRedemption = bizRedemption;
    }

    public Integer getBizRedemption() 
    {
        return bizRedemption;
    }

    public void setBizHash(String bizHash) 
    {
        this.bizHash = bizHash;
    }

    public String getBizHash() 
    {
        return bizHash;
    }

    public void setChainOrderId(String chainOrderId) 
    {
        this.chainOrderId = chainOrderId;
    }

    public String getChainOrderId() 
    {
        return chainOrderId;
    }

    public void setChainUserAddress(String chainUserAddress) 
    {
        this.chainUserAddress = chainUserAddress;
    }

    public String getChainUserAddress() 
    {
        return chainUserAddress;
    }

    public void setChainAmount(BigDecimal chainAmount) 
    {
        this.chainAmount = chainAmount;
    }

    public BigDecimal getChainAmount() 
    {
        return chainAmount;
    }

    public void setChainRedemption(Integer chainRedemption) 
    {
        this.chainRedemption = chainRedemption;
    }

    public Integer getChainRedemption() 
    {
        return chainRedemption;
    }

    public void setChainTxHash(String chainTxHash) 
    {
        this.chainTxHash = chainTxHash;
    }

    public String getChainTxHash() 
    {
        return chainTxHash;
    }

    public void setChainBlockNumber(Long chainBlockNumber) 
    {
        this.chainBlockNumber = chainBlockNumber;
    }

    public Long getChainBlockNumber() 
    {
        return chainBlockNumber;
    }

    public void setChainLogIndex(Long chainLogIndex) 
    {
        this.chainLogIndex = chainLogIndex;
    }

    public Long getChainLogIndex() 
    {
        return chainLogIndex;
    }

    public void setChainExecutor(String chainExecutor) 
    {
        this.chainExecutor = chainExecutor;
    }

    public String getChainExecutor() 
    {
        return chainExecutor;
    }

    public void setChainTimestamp(Date chainTimestamp) 
    {
        this.chainTimestamp = chainTimestamp;
    }

    public Date getChainTimestamp() 
    {
        return chainTimestamp;
    }

    public void setChainContractType(String chainContractType) 
    {
        this.chainContractType = chainContractType;
    }

    public String getChainContractType() 
    {
        return chainContractType;
    }

    public void setChainContractAddress(String chainContractAddress) 
    {
        this.chainContractAddress = chainContractAddress;
    }

    public String getChainContractAddress() 
    {
        return chainContractAddress;
    }

    public void setReconcileStatusMatch(Integer reconcileStatusMatch) 
    {
        this.reconcileStatusMatch = reconcileStatusMatch;
    }

    public Integer getReconcileStatusMatch() 
    {
        return reconcileStatusMatch;
    }

    public void setReconcileAmountMatch(Integer reconcileAmountMatch) 
    {
        this.reconcileAmountMatch = reconcileAmountMatch;
    }

    public Integer getReconcileAmountMatch() 
    {
        return reconcileAmountMatch;
    }

    public void setReconcileUserMatch(Integer reconcileUserMatch) 
    {
        this.reconcileUserMatch = reconcileUserMatch;
    }

    public Integer getReconcileUserMatch() 
    {
        return reconcileUserMatch;
    }

    public void setReconcileHashMatch(Integer reconcileHashMatch) 
    {
        this.reconcileHashMatch = reconcileHashMatch;
    }

    public Integer getReconcileHashMatch() 
    {
        return reconcileHashMatch;
    }

    public void setReconcileRedemptionMatch(Integer reconcileRedemptionMatch) 
    {
        this.reconcileRedemptionMatch = reconcileRedemptionMatch;
    }

    public Integer getReconcileRedemptionMatch() 
    {
        return reconcileRedemptionMatch;
    }

    public void setReconcileStatus(Integer reconcileStatus) 
    {
        this.reconcileStatus = reconcileStatus;
    }

    public Integer getReconcileStatus() 
    {
        return reconcileStatus;
    }

    public void setReconcileType(Integer reconcileType) 
    {
        this.reconcileType = reconcileType;
    }

    public Integer getReconcileType() 
    {
        return reconcileType;
    }

    public void setReconcileRemark(String reconcileRemark) 
    {
        this.reconcileRemark = reconcileRemark;
    }

    public String getReconcileRemark() 
    {
        return reconcileRemark;
    }

    public void setReconcileTime(Date reconcileTime) 
    {
        this.reconcileTime = reconcileTime;
    }

    public Date getReconcileTime() 
    {
        return reconcileTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("flag", getFlag())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("bizOrderNumber", getBizOrderNumber())
                .append("bizOrderExists", getBizOrderExists())
                .append("bizOriginType", getBizOriginType())
                .append("bizUserId", getBizUserId())
                .append("bizCoinId", getBizCoinId())
                .append("bizToAddress", getBizToAddress())
                .append("bizStatus", getBizStatus())
                .append("bizAmount", getBizAmount())
                .append("bizRedemption", getBizRedemption())
                .append("bizHash", getBizHash())
                .append("chainOrderId", getChainOrderId())
                .append("chainUserAddress", getChainUserAddress())
                .append("chainAmount", getChainAmount())
                .append("chainRedemption", getChainRedemption())
                .append("chainTxHash", getChainTxHash())
                .append("chainBlockNumber", getChainBlockNumber())
                .append("chainLogIndex", getChainLogIndex())
                .append("chainExecutor", getChainExecutor())
                .append("chainTimestamp", getChainTimestamp())
                .append("chainContractType", getChainContractType())
                .append("chainContractAddress", getChainContractAddress())
                .append("reconcileStatusMatch", getReconcileStatusMatch())
                .append("reconcileAmountMatch", getReconcileAmountMatch())
                .append("reconcileUserMatch", getReconcileUserMatch())
                .append("reconcileHashMatch", getReconcileHashMatch())
                .append("reconcileRedemptionMatch", getReconcileRedemptionMatch())
                .append("reconcileStatus", getReconcileStatus())
                .append("reconcileType", getReconcileType())
                .append("reconcileRemark", getReconcileRemark())
                .append("reconcileTime", getReconcileTime())
                .toString();
    }
}
