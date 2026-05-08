package com.ruoyi.system.service;


import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.BscWithdrawalLog;
import com.ruoyi.system.domain.BscWithdrawalSign;
import com.ruoyi.system.mapper.BscWithdrawalLogMapper;
import com.ruoyi.system.mapper.BscWithdrawalSignMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Slf4j
@Service
public class BscWithdrawalSignServiceImpl {


    // ==================== 签名进度状态常量 ====================
    /**
     * 签名成功
     */
    private static final int SIGN_SUCCESS = 2;

    // ==================== 依赖注入 ====================


    @Resource
    private AccessControlServiceImpl accessControlService;



    // ==================== 配置参数 ====================

    /**
     * 当前签名服务属于第几步
     */
    @Value("${withdraw.audit.step}")
    private Integer auditStep;



    /**
     * 代币精度，仅 amountIsRaw=false 时使用
     */
    @Value("${contract.exchangeGradeWithdraw.token-decimals:18}")
    private Integer tokenDecimals;
    @Resource
    private BscWithdrawalSignMapper bscWithdrawalSignMapper;
    @Resource
    private BscWithdrawalLogMapper bscWithdrawalLogMapper;

    // ==================== 公开方法 ====================

    /**
     * 更新签名成功
     *
     * @param sign      签名记录
     * @param withdrawalLog     提现记录
     */
    public void markSuccess(BscWithdrawalSign sign, BscWithdrawalLog withdrawalLog) {
        BscWithdrawalSign update = new BscWithdrawalSign();
        update.setId(sign.getId());
        update.setSignStatus(2);
        update.setUpdateTime(LocalDateTime.now());
        bscWithdrawalSignMapper.updateById(update);

        // 更新提现主表签名进度状态
        BscWithdrawalLog bscWithdrawalLog = new BscWithdrawalLog();
        bscWithdrawalLog.setId(withdrawalLog.getId());
        bscWithdrawalLog.setLargeAmountPassed(1);
        updateSignProgress(bscWithdrawalLog, auditStep);
        bscWithdrawalLogMapper.updateById(bscWithdrawalLog);


        log.info("签名成功，signId={}, signerAddress={}", sign.getId(), sign.getSignerAddress());
    }

    /**
     * 实际签名逻辑
     *
     * @param sign             当前签名记录
     * @param bscWithdrawalLog 提现主表记录
     */
    public void doApproveAndSign(BscWithdrawalSign sign, BscWithdrawalLog bscWithdrawalLog,String contractAddress) {
        try {
            // 6. 校验当前签名地址是否为链上授权 signer
            Boolean isSigner = accessControlService.isSigner(sign.getSignerAddress(),contractAddress);
            if (isSigner == null || !isSigner) {
                throw new ServiceException("当前签名地址不是链上授权signer，signerAddress=" + sign.getSignerAddress());
            }
            // 7. 更新签名记录为成功
            markSuccess(sign, bscWithdrawalLog);
            // 8. 统计当前订单已成功签名数量
            long signedCount = bscWithdrawalSignMapper.countByWithdrawLogIdAndDigest(
                    bscWithdrawalLog.getId(),
                    sign.getSignDigest()
            );
            if (signedCount <= 0) {
                throw new ServiceException("签名摘要无效，signerAddress=" + sign.getSignerAddress());
            }
            BigInteger amount = convertAmount(bscWithdrawalLog.getAmount());
            // 9. 读取链上要求签名数量
            BigInteger requiredCount = accessControlService.requiredSignaturesForAmount(amount,contractAddress);
            // 10. 达到门槛后，更新提现主表 signFinished=1
            if (requiredCount != null && signedCount >= requiredCount.longValue()) {
                BscWithdrawalLog updateLog = new BscWithdrawalLog();
                updateLog.setId(bscWithdrawalLog.getId());
                updateLog.setSignFinished(1);
                bscWithdrawalLogMapper.updateById(updateLog);
                log.info("签名数量已达要求，可执行发币，orderNo={}, signedCount={}, requiredCount={}",
                        bscWithdrawalLog.getOrderNumber(), signedCount, requiredCount);
            }
            log.info("执行签名完成，signId={}, orderNo={}, signedCount={}, requiredCount={}",
                    sign.getId(), bscWithdrawalLog.getOrderNumber(), signedCount, requiredCount);

        } catch (Exception e) {
            //markFail(sign.getId(), bscWithdrawalLog.getId(), buildSimpleErrorMsg(e));
            log.error("签名执行异常，signId={}, orderNo={}", sign.getId(), bscWithdrawalLog.getOrderNumber(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 更新签名进度状态
     */
    private void updateSignProgress(BscWithdrawalLog log, int step) {
        switch (step) {
            case 1:
                log.setSignProgressOne(BscWithdrawalSignServiceImpl.SIGN_SUCCESS);
                break;
            case 2:
                log.setSignProgressTwo(BscWithdrawalSignServiceImpl.SIGN_SUCCESS);
                break;
            case 3:
                log.setSignProgressThree(BscWithdrawalSignServiceImpl.SIGN_SUCCESS);
                break;
            case 4:
                log.setSignProgressFour(BscWithdrawalSignServiceImpl.SIGN_SUCCESS);
                break;
            default:
                throw new IllegalArgumentException("无效的签名步骤: " + step);
        }
    }

    /**
     * 将数据库金额转换为链上 uint256 金额
     */
    private BigInteger convertAmount(BigDecimal dbAmount) {
        if (dbAmount == null) {
            throw new RuntimeException("amount不能为空");
        }
        BigDecimal base = BigDecimal.TEN.pow(tokenDecimals);
        return dbAmount.multiply(base).toBigInteger();
    }


}


