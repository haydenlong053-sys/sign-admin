package com.ruoyi.server.service;

import com.ruoyi.server.domain.WithdrawReconcileLog;
import com.ruoyi.server.domain.WithdrawReconcileStat;
import com.ruoyi.server.domain.WithdrawReconcileStatDTO;

import java.util.List;
import java.util.Map;

/**
 * BSC提现对账记录 服务层
 * 
 * @author ruoyi
 */
public interface IWithdrawReconcileService
{
    /**
     * 查询BSC提现对账记录列表
     * 
     * @param withdrawReconcileLog BSC提现对账记录
     * @return BSC提现对账记录集合
     */
    List<WithdrawReconcileLog> selectWithdrawReconcileLogList(WithdrawReconcileLog withdrawReconcileLog);

    /**
     * 获取出账统计数据（按币种+时间维度：总计/昨日/当日）
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 出账统计数据列表
     */
    List<WithdrawReconcileStatDTO> getOutboundStats(String startDate, String endDate);
}
