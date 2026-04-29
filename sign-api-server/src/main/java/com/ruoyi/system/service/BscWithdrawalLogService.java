package com.ruoyi.system.service;

import com.ruoyi.system.domain.BscWithdrawalLog;
import com.ruoyi.system.mapper.BscWithdrawalLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * BSC 提现日志
 */
@Service
public class BscWithdrawalLogService {

    @Autowired
    private BscWithdrawalLogMapper bscWithdrawalLogMapper;

    public List<BscWithdrawalLog> selectBscWithdrawalLogList(BscWithdrawalLog query) {
        return bscWithdrawalLogMapper.selectBscWithdrawalLogList(query);
    }

    public BscWithdrawalLog selectBscWithdrawalLogById(Long id) {
        return bscWithdrawalLogMapper.selectBscWithdrawalLogById(id);
    }
}
