package com.ruoyi.system.service;


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

    /**
     * 签名失败
     */
    private static final int SIGN_FAILED = 3;

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
     * 当前签名服务器名称
     */
    @Value("${withdraw.audit.server-name}")
    private String auditServerName;

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
     * 更新签名失败
     * 失败原因只保存摘要，避免字段过长。
     *
     * @param signId 签名记录ID
     * @param logId  提现记录ID
     * @param reason 失败原因
     */
    public void markFail(Integer signId, Long logId, String reason) {
        if (signId == null || logId == null) {
            return;
        }
        BscWithdrawalSign update = new BscWithdrawalSign();
        update.setId(signId);
        update.setSignStatus(3);
        update.setUpdateTime(LocalDateTime.now());
        update.setFailReason(safeReason(reason));
        bscWithdrawalSignMapper.updateById(update);

        // 更新提现主表签名进度状态
        BscWithdrawalLog bscWithdrawalLog = new BscWithdrawalLog();
        bscWithdrawalLog.setId(logId);
        updateSignProgress(bscWithdrawalLog, auditStep, SIGN_FAILED);
        bscWithdrawalLogMapper.updateById(bscWithdrawalLog);

        log.error("签名失败，signId={}, reason={}", signId, reason);
    }

    /**
     * 更新签名成功
     *
     * @param sign      签名记录
     * @param withdrawalLog     提现记录
     * @param userNonce 用户nonce
     * @param orderNo   订单号
     */
    public void markSuccess(BscWithdrawalSign sign, BscWithdrawalLog withdrawalLog, BigInteger userNonce, String orderNo) {
        BscWithdrawalSign update = new BscWithdrawalSign();
        update.setId(sign.getId());
        update.setSignStatus(2);
        update.setUpdateTime(LocalDateTime.now());
        update.setUserNonce(userNonce);
        bscWithdrawalSignMapper.updateById(update);

        // 更新提现主表签名进度状态
        BscWithdrawalLog bscWithdrawalLog = new BscWithdrawalLog();
        bscWithdrawalLog.setId(withdrawalLog.getId());
        updateSignProgress(bscWithdrawalLog, auditStep, SIGN_SUCCESS);
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
                throw new RuntimeException("当前签名地址不是链上授权signer，signerAddress=" + sign.getSignerAddress());
            }
            // 7. 更新签名记录为成功
            markSuccess(sign, bscWithdrawalLog, null, bscWithdrawalLog.getOrderNumber());
            // 8. 统计当前订单已成功签名数量
            long signedCount = bscWithdrawalSignMapper.countByWithdrawLogIdAndDigest(
                    bscWithdrawalLog.getId(),
                    sign.getSignDigest()
            );
            if (signedCount <= 0) {
                throw new RuntimeException("签名摘要无效，signerAddress=" + sign.getSignerAddress());
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
            markFail(sign.getId(), bscWithdrawalLog.getId(), buildSimpleErrorMsg(e));
            log.error("签名执行异常，signId={}, orderNo={}", sign.getId(), bscWithdrawalLog.getOrderNumber(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 更新签名进度状态
     */
    private void updateSignProgress(BscWithdrawalLog log, int step, int status) {
        switch (step) {
            case 1:
                log.setSignProgressOne(status);
                break;
            case 2:
                log.setSignProgressTwo(status);
                break;
            case 3:
                log.setSignProgressThree(status);
                break;
            case 4:
                log.setSignProgressFour(status);
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


    /**
     * 构建简短异常信息
     */
    private String buildSimpleErrorMsg(Exception e) {
        if (e == null) {
            return "未知异常";
        }

        String msg = e.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            msg = e.getClass().getSimpleName();
        } else {
            msg = e.getClass().getSimpleName() + ": " + msg;
        }

        return safeReason(msg);
    }

    /**
     * 失败原因截断，避免超过数据库字段长度
     */
    private String safeReason(String reason) {
        if (reason == null) {
            return null;
        }

        reason = reason.trim();
        int maxLen = 500;

        if (reason.length() <= maxLen) {
            return reason;
        }

        return reason.substring(0, maxLen);
    }
}


