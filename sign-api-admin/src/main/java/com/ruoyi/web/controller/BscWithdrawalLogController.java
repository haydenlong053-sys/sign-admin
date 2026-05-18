package com.ruoyi.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.server.domain.BscWithdrawalLog;
import com.ruoyi.common.req.WithdrawRequest;
import com.ruoyi.common.req.BscWithdrawalSignSubmit;
import com.ruoyi.server.service.impl.BscWithdrawalLogServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * BSC 大额提现（四号待签）列表与签名占位流程
 */
@Controller
@RequestMapping("/project/bscWithdrawalLog")
public class BscWithdrawalLogController extends BaseController {

    private final String prefix = "project/bscWithdrawalLog";

    @Autowired
    private BscWithdrawalLogServiceImpl bscWithdrawalLogService;

    @RequiresPermissions("project:bscWithdrawalLog:view")
    @GetMapping()
    public String page() {
        return prefix + "/bscWithdrawalLogList";
    }

    /**
     * 分页列表：固定条件 is_large_amount=1、sign_progress_four=0、flag=0
     */
    @RequiresPermissions("project:bscWithdrawalLog:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BscWithdrawalLog query) {
        startPage();
        List<BscWithdrawalLog> list = bscWithdrawalLogService.selectBscWithdrawalLogList(query);
        return getDataTable(list);
    }

    /**
     * 获取签名参数
     */
    @RequiresPermissions("project:bscWithdrawalLog:sign")
    @PostMapping("/signParams/{id}")
    @ResponseBody
    public AjaxResult signParams(@PathVariable("id") Long id) throws Exception {
        BscWithdrawalLog withdrawalLog = bscWithdrawalLogService.selectBscWithdrawalLogById(id);
        if (withdrawalLog == null) {
            return AjaxResult.error("记录不存在或不符合大额四号待签条件");
        }
        WithdrawRequest withdrawRequest = bscWithdrawalLogService.buildWithdrawRequest(withdrawalLog);
        Map<String, Object> typedData = bscWithdrawalLogService.buildWithdrawTypedData(withdrawRequest, withdrawalLog.getCoinId());
        return AjaxResult.success("操作成功")
                .put("signPayload", typedData)
                .put("id", withdrawalLog.getId());
    }


    /**
     * 提交签名结果（占位：直接成功）
     */
    @RequiresPermissions("project:bscWithdrawalLog:sign")
    @Log(title = "BSC大额提现签名提交", businessType = BusinessType.UPDATE)
    @PostMapping("/submitSign")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submitSign(@RequestBody BscWithdrawalSignSubmit withdrawalAuditReq){
        if (withdrawalAuditReq == null || withdrawalAuditReq.getWithdrawalLogId() == null) {
            return AjaxResult.error("参数异常：withdrawalLogId 不能为空");
        }
        if (withdrawalAuditReq.getApproved() == null) {
            return AjaxResult.error("参数异常：approved 不能为空（1 通过 0 驳回）");
        }
        // 审核通过：必须有钱包地址并完成 EIP-712 签名；驳回：不传签名与地址
        if (Integer.valueOf(1).equals(withdrawalAuditReq.getApproved())) {
            if (StringUtils.isBlank(withdrawalAuditReq.getSignerAddress())) {
                return error("签名钱包地址为空");
            }
            if (!withdrawalAuditReq.getSignerAddress().equalsIgnoreCase("0x71c7fcc1206f7df0992ec9436cf5128215a1c69e")) {
                return error("请用大额审核 0x71c7fcc1206f7df0992ec9436cf5128215a1c69e 审核");
            }
        }
        logger.info("签名提交{}", JSONObject.toJSONString(withdrawalAuditReq));
        return bscWithdrawalLogService.submitSign(withdrawalAuditReq);
    }
}
