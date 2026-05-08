package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.BaseUtil;
import com.ruoyi.system.domain.BscWithdrawalLog;
import com.ruoyi.system.domain.BscWithdrawalSign;
import com.ruoyi.system.domain.WithdrawRequest;
import com.ruoyi.system.domain.req.BscWithdrawalSignSubmit;
import com.ruoyi.system.mapper.BscWithdrawalLogMapper;
import com.ruoyi.system.mapper.BscWithdrawalSignMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-22 15:59
 */
@Slf4j
@Service
public class BscWithdrawalLogServiceImpl {


    @Value("${withdraw.audit.server-name}")
    private String auditServerName;
    /**
     * 当前签名服务属于第几步
     */
    @Value("${withdraw.audit.step}")
    private Integer auditStep;
    @Value("${contract.exchangeGradeWithdraw.contract-withdraw-u}")
    private String contractWithdrawUsdt;
    @Value("${contract.exchangeGradeWithdraw.contract-withdraw-oidc}")
    private String contractWithdrawOidc;

    @Resource
    private BscWithdrawalSignServiceImpl bscWithdrawalSignService;
    @Resource
    private BscWithdrawalLogMapper bscWithdrawalLogMapper;
    @Resource
    private BscWithdrawalSignMapper bscWithdrawalSignMapper;
    @Resource
    private AccessControlServiceImpl accessControlService;


    public List<BscWithdrawalLog> selectBscWithdrawalLogList(BscWithdrawalLog query) {
        return bscWithdrawalLogMapper.selectBscWithdrawalLogList(query);
    }

    public BscWithdrawalLog selectBscWithdrawalLogById(Long id) {
        return bscWithdrawalLogMapper.selectBscWithdrawalLogById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submitSign(BscWithdrawalSignSubmit withdrawalAuditReq) {
        // 1. 参数校验
        if (withdrawalAuditReq == null || withdrawalAuditReq.getWithdrawalLogId() == null) {
            return AjaxResult.error("参数异常");
        }
        // 2. 查询提现记录
        BscWithdrawalLog withdrawalLog = bscWithdrawalLogMapper.getById(withdrawalAuditReq.getWithdrawalLogId());
        if (withdrawalLog == null) {
            return AjaxResult.error("提现记录不存在");
        }
        if (withdrawalLog.getIsLargeAmount() != 1) {
            return AjaxResult.error("该订单不是大额订单");
        }
        // 3. 审核拒绝
        if (withdrawalAuditReq.getApproved() != null && withdrawalAuditReq.getApproved() == 0) {
            log.info("审核拒绝，withdrawalLogId={}", withdrawalAuditReq.getWithdrawalLogId());
            withdrawalLog.setLargeAmountPassed(2);
            bscWithdrawalLogMapper.updateById(withdrawalLog);
            return AjaxResult.success("审核拒绝 更新订单成功");
        }
        // 4. 审核通过（执行签名）
        if (withdrawalAuditReq.getApproved() != null && withdrawalAuditReq.getApproved() == 1) {
            String orderNo = String.valueOf(withdrawalLog.getOrderNumber());
            String contractAddress;
            if (BaseUtil.Base_HasValue(withdrawalLog.getCoinId()) && withdrawalLog.getCoinId() == 1) {
                contractAddress = contractWithdrawUsdt;
            } else {
                contractAddress = contractWithdrawOidc;
            }
            try {
                // 8. 检查是否有重复的签名记录
                long existCount = bscWithdrawalSignMapper.countSuccessSign(
                        withdrawalLog.getId(),
                        auditStep,
                        auditServerName
                );
                if (existCount > 0) {
                    log.info("签名记录已存在，withdrawalLogId={}", withdrawalLog.getId());
                    return AjaxResult.success("签名任务已受理");
                }
                WithdrawRequest req = buildWithdrawRequest(withdrawalLog);
                //直接执行签名的校验
                boolean verify = Eip712VerifyUtil.verify(contractAddress,
                        withdrawalAuditReq.getSignerAddress(),
                        withdrawalAuditReq.getSignature(),
                        req);
                if (!verify) {
                    return AjaxResult.success("验签失败");
                }
                BscWithdrawalSign sign = new BscWithdrawalSign(withdrawalLog, withdrawalAuditReq, auditStep, auditServerName);
                sign.setSignDigest(signWithdrawRequest(req, contractAddress));
                bscWithdrawalSignMapper.insert(sign);
                // 12. 执行签名
                bscWithdrawalSignService.doApproveAndSign(sign, withdrawalLog, contractAddress);
                log.info("审核通过，签名任务执行成功，orderNo={}", orderNo);
                return AjaxResult.success("审核通过，签名成功");

            } catch (Exception e) {
                log.error("审核签名执行异常，withdrawalLogId={}", withdrawalLog.getId(), e);
                return AjaxResult.error("签名执行失败：" + e.getMessage());
            }
        }
        return AjaxResult.error("审核状态异常");
    }


    public WithdrawRequest buildWithdrawRequest(BscWithdrawalLog bscWithdrawalLog) {
        BigInteger orderId = BigInteger.valueOf(Long.parseLong(bscWithdrawalLog.getOrderNumber()));
        BigInteger amount = convertAmount(bscWithdrawalLog.getAmount());
        BigInteger redemption = BigInteger.valueOf(bscWithdrawalLog.getRedemption().longValue());
        BigInteger deadline = bscWithdrawalLog.getDeadline();
        BigInteger bizIdValue = BigInteger.valueOf(bscWithdrawalLog.getCoinId());
        return new WithdrawRequest(orderId, bscWithdrawalLog.getToAddress(), amount,
                redemption, deadline, bizIdValue);
    }

    /**
     * 对提现请求进行 EIP-712 签名
     */
    public String signWithdrawRequest(WithdrawRequest req,String contractAddress) throws Exception {
        log.info("提现请求1: orderId={}, user={}, amount={}, redemption={}, deadline={},  bizId={}",
                req.getOrderId(), req.getUser(), req.getAmount(), req.getRedemption(),
                req.getDeadline(), req.getBizId());
        return accessControlService.hashWithdrawRequest(req, contractAddress);
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

    /**
     * 构建 MetaMask eth_signTypedData_v4 需要的 EIP-712 typedData
     */
    public Map<String, Object> buildWithdrawTypedData(WithdrawRequest req, Integer coinId) {
        Map<String, Object> typedData = new LinkedHashMap<>();

        Map<String, Object> types = new LinkedHashMap<>();

        List<Map<String, String>> domainTypes = new ArrayList<>();
        domainTypes.add(typeField("name", "string"));
        domainTypes.add(typeField("version", "string"));
        domainTypes.add(typeField("chainId", "uint256"));
        domainTypes.add(typeField("verifyingContract", "address"));

        List<Map<String, String>> withdrawTypes = new ArrayList<>();
        withdrawTypes.add(typeField("orderId", "uint256"));
        withdrawTypes.add(typeField("user", "address"));
        withdrawTypes.add(typeField("amount", "uint256"));
        withdrawTypes.add(typeField("redemption", "uint8"));
        withdrawTypes.add(typeField("deadline", "uint256"));
        withdrawTypes.add(typeField("bizId", "uint256"));

        types.put("EIP712Domain", domainTypes);
        types.put("WithdrawRequest", withdrawTypes);
        String contractAddress;
        if (BaseUtil.Base_HasValue(coinId) && coinId == 1) {
            contractAddress = contractWithdrawUsdt;
        } else {
            contractAddress = contractWithdrawOidc;
        }

        Map<String, Object> domain = new LinkedHashMap<>();
        domain.put("name", "ExchangeGradeWithdraw");
        domain.put("version", "1");
        domain.put("chainId", 56);
        domain.put("verifyingContract", contractAddress);

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("orderId", req.getOrderId().toString());
        message.put("user", req.getUser());
        message.put("amount", req.getAmount().toString());
        message.put("redemption", req.getRedemption().intValue());
        message.put("deadline", req.getDeadline().toString());
        message.put("bizId", req.getBizId().toString());

        typedData.put("types", types);
        typedData.put("primaryType", "WithdrawRequest");
        typedData.put("domain", domain);
        typedData.put("message", message);

        return typedData;
    }

    private Map<String, String> typeField(String name, String type) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("name", name);
        map.put("type", type);
        return map;
    }
}
