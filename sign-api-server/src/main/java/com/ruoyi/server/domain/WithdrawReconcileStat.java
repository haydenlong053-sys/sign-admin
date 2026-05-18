package com.ruoyi.server.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提现对账每日统计对象 withdraw_reconcile_stat
 * 
 * @author ruoyi
 */
public class WithdrawReconcileStat extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 统计日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "统计日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date statDate;

    /** 币种ID：1 USDT 2 ODIC */
    @Excel(name = "币种ID")
    private Integer coinId;

    /** 币种名称 */
    @Excel(name = "币种名称")
    private String coinName;

    /** 当日核对提币总笔数 */
    @Excel(name = "总笔数")
    private Integer totalCount;

    /** 当日核对提币总金额 */
    @Excel(name = "总金额")
    private BigDecimal totalAmount;

    /** 对账成功订单数 */
    @Excel(name = "成功笔数")
    private Integer successCount;

    /** 对账成功金额 */
    @Excel(name = "成功金额")
    private BigDecimal successAmount;

    /** 异常订单数 */
    @Excel(name = "异常笔数")
    private Integer exceptionCount;

    /** 异常订单金额 */
    @Excel(name = "异常金额")
    private BigDecimal exceptionAmount;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setStatDate(Date statDate) 
    {
        this.statDate = statDate;
    }

    public Date getStatDate() 
    {
        return statDate;
    }

    public void setCoinId(Integer coinId) 
    {
        this.coinId = coinId;
    }

    public Integer getCoinId() 
    {
        return coinId;
    }

    public void setCoinName(String coinName) 
    {
        this.coinName = coinName;
    }

    public String getCoinName() 
    {
        return coinName;
    }

    public void setTotalCount(Integer totalCount) 
    {
        this.totalCount = totalCount;
    }

    public Integer getTotalCount() 
    {
        return totalCount;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setSuccessCount(Integer successCount) 
    {
        this.successCount = successCount;
    }

    public Integer getSuccessCount() 
    {
        return successCount;
    }

    public void setSuccessAmount(BigDecimal successAmount) 
    {
        this.successAmount = successAmount;
    }

    public BigDecimal getSuccessAmount() 
    {
        return successAmount;
    }

    public void setExceptionCount(Integer exceptionCount) 
    {
        this.exceptionCount = exceptionCount;
    }

    public Integer getExceptionCount() 
    {
        return exceptionCount;
    }

    public void setExceptionAmount(BigDecimal exceptionAmount) 
    {
        this.exceptionAmount = exceptionAmount;
    }

    public BigDecimal getExceptionAmount() 
    {
        return exceptionAmount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("statDate", getStatDate())
                .append("coinId", getCoinId())
                .append("coinName", getCoinName())
                .append("totalCount", getTotalCount())
                .append("totalAmount", getTotalAmount())
                .append("successCount", getSuccessCount())
                .append("successAmount", getSuccessAmount())
                .append("exceptionCount", getExceptionCount())
                .append("exceptionAmount", getExceptionAmount())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
