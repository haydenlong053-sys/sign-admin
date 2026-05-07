package com.ruoyi.system.service;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.BscWithdrawalLog;
import com.ruoyi.system.domain.BscWithdrawalSign;
import com.ruoyi.system.domain.WithdrawRequest;
import com.ruoyi.system.domain.req.BscWithdrawalSignSubmit;
import com.ruoyi.system.mapper.BscWithdrawalLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * BSC 提现日志
 */
@Service
@Slf4j
public class BscWithdrawalLogService {

    @Autowired
    private BscWithdrawalLogMapper bscWithdrawalLogMapper;

    public List<BscWithdrawalLog> selectBscWithdrawalLogList(BscWithdrawalLog query) {
        return bscWithdrawalLogMapper.selectBscWithdrawalLogList(query);
    }

    public BscWithdrawalLog selectBscWithdrawalLogById(Long id) {
        return bscWithdrawalLogMapper.selectBscWithdrawalLogById(id);
    }

    public AjaxResult submitSign(BscWithdrawalSignSubmit withdrawalAuditReq) {
        // 1. 参数校验
        if (withdrawalAuditReq == null || withdrawalAuditReq.getWithdrawalLogId() == null) {
            return AjaxResult.error("参数异常");
        }
        // 2. 查询提现记录
        BscWithdrawalLog withdrawalLog = bscWithdrawalLogMapper.selectBscWithdrawalLogById(withdrawalAuditReq.getWithdrawalLogId());
        if (withdrawalLog == null) {
            return AjaxResult.error("提现记录不存在");
        }
        if (withdrawalLog.getIsLargeAmount() != 1) {
            return AjaxResult.error("该订单不是大额订单");
        }
        WithdrawRequest req=buildWithdrawRequest(withdrawalLog);
        //直接执行签名的校验
        boolean verify = Eip712VerifyUtil.verify("0x17f4302FBE11dfc66b9eBE45D4b8919f042F7909",
                withdrawalAuditReq.getSignerAddress(),
                withdrawalAuditReq.getSignature(),
                req);
        return AjaxResult.success("签名校验结果",verify);
    }

    public WithdrawRequest buildWithdrawRequest(BscWithdrawalLog bscWithdrawalLog) {
        BigInteger orderId = BigInteger.valueOf(bscWithdrawalLog.getOrderNumber());
        BigInteger amount = convertAmount(bscWithdrawalLog.getAmount());
        BigInteger redemption = BigInteger.valueOf(bscWithdrawalLog.getRedemption().longValue());
        BigInteger deadline = bscWithdrawalLog.getDeadline();
        BigInteger bizIdValue = BigInteger.valueOf(1);
        return new WithdrawRequest(orderId, bscWithdrawalLog.getToAddress(), amount,
                redemption, deadline, bizIdValue);
    }

    /**
     * 对提现请求进行 EIP-712 签名
     */
    public String signWithdrawRequest(WithdrawRequest req) throws Exception {
        // 1. 构建 EIP-712 domainSeparator
        byte[] domainSeparator = Eip712Helper.buildDomainSeparator(BigInteger.valueOf(56), "0x17f4302FBE11dfc66b9eBE45D4b8919f042F7909");

        // 2. 构建 WithdrawRequest structHash
        byte[] structHash = Eip712Helper.buildWithdrawStructHash(req);

        // 3. 构建最终 EIP-712 digest
        byte[] eip712Digest = Eip712Helper.buildEip712Digest(domainSeparator, structHash);
        return Numeric.toHexString(eip712Digest);
    }

    /**
     * 将数据库金额转换为链上 uint256 金额
     */
    private BigInteger convertAmount(BigDecimal dbAmount) {
        if (dbAmount == null) {
            throw new RuntimeException("amount不能为空");
        }
        BigDecimal base = BigDecimal.TEN.pow(18);
        return dbAmount.multiply(base).toBigInteger();
    }
}
