package com.ruoyi.server.domain;

import java.math.BigDecimal;

/**
 * 出账统计DTO - 用于前端卡片展示
 * 
 * @author ruoyi
 */
public class WithdrawReconcileStatDTO
{
    /** 币种ID */
    private Integer coinId;

    /** 币种名称 */
    private String coinName;

    /** 总出账笔数 */
    private Integer totalOutCount;

    /** 总出账金额 */
    private BigDecimal totalOutAmount;

    /** 昨日出账笔数 */
    private Integer yesterdayOutCount;

    /** 昨日出账金额 */
    private BigDecimal yesterdayOutAmount;

    /** 当日出账笔数 */
    private Integer todayOutCount;

    /** 当日出账金额 */
    private BigDecimal todayOutAmount;

    public Integer getCoinId() {
        return coinId;
    }

    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public Integer getTotalOutCount() {
        return totalOutCount;
    }

    public void setTotalOutCount(Integer totalOutCount) {
        this.totalOutCount = totalOutCount;
    }

    public BigDecimal getTotalOutAmount() {
        return totalOutAmount;
    }

    public void setTotalOutAmount(BigDecimal totalOutAmount) {
        this.totalOutAmount = totalOutAmount;
    }

    public Integer getYesterdayOutCount() {
        return yesterdayOutCount;
    }

    public void setYesterdayOutCount(Integer yesterdayOutCount) {
        this.yesterdayOutCount = yesterdayOutCount;
    }

    public BigDecimal getYesterdayOutAmount() {
        return yesterdayOutAmount;
    }

    public void setYesterdayOutAmount(BigDecimal yesterdayOutAmount) {
        this.yesterdayOutAmount = yesterdayOutAmount;
    }

    public Integer getTodayOutCount() {
        return todayOutCount;
    }

    public void setTodayOutCount(Integer todayOutCount) {
        this.todayOutCount = todayOutCount;
    }

    public BigDecimal getTodayOutAmount() {
        return todayOutAmount;
    }

    public void setTodayOutAmount(BigDecimal todayOutAmount) {
        this.todayOutAmount = todayOutAmount;
    }
}
