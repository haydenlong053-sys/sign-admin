package com.ruoyi.server.mapper;

import com.ruoyi.server.domain.BscWithdrawalLog;

import java.util.List;

/**
 * BSC 提现日志 Mapper
 */
public interface BscWithdrawalLogMapper {

    /**
     * 列表（Mapper 内固定：大额且四号待签、未删除）
     */
    List<BscWithdrawalLog> selectBscWithdrawalLogList(BscWithdrawalLog query);

    BscWithdrawalLog selectBscWithdrawalLogById(Long id);

    BscWithdrawalLog getById(Long id);

    int  updateById(BscWithdrawalLog withdrawalLog);
}
