package com.ruoyi.server.service.impl;

import com.ruoyi.server.domain.WithdrawReconcileLog;
import com.ruoyi.server.domain.WithdrawReconcileStat;
import com.ruoyi.server.domain.WithdrawReconcileStatDTO;
import com.ruoyi.server.mapper.WithdrawReconcileMapper;
import com.ruoyi.server.service.IWithdrawReconcileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * BSC提现对账记录 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class WithdrawReconcileServiceImpl implements IWithdrawReconcileService
{
    @Autowired
    private WithdrawReconcileMapper withdrawReconcileMapper;

    /**
     * 查询BSC提现对账记录列表
     * 
     * @param withdrawReconcileLog BSC提现对账记录
     * @return BSC提现对账记录集合
     */
    @Override
    public List<WithdrawReconcileLog> selectWithdrawReconcileLogList(WithdrawReconcileLog withdrawReconcileLog)
    {
        return withdrawReconcileMapper.selectWithdrawReconcileLogList(withdrawReconcileLog);
    }

    /**
     * 获取出账统计数据（按币种+时间维度：总计/昨日/当日）
     * 
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 出账统计数据列表
     */
    @Override
    public List<WithdrawReconcileStatDTO> getOutboundStats(String startDate, String endDate)
    {
        return withdrawReconcileMapper.selectOutboundStats(startDate, endDate);
    }
}
