package com.ruoyi.web.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.BscWithdrawalLog;
import com.ruoyi.system.domain.WithdrawRequest;
import com.ruoyi.system.domain.req.BscWithdrawalSignSubmit;
import com.ruoyi.system.service.BscWithdrawalLogServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        BscWithdrawalLog row = bscWithdrawalLogService.selectBscWithdrawalLogById(id);
        if (row == null) {
            return AjaxResult.error("记录不存在或不符合大额四号待签条件");
        }
        WithdrawRequest withdrawRequest = bscWithdrawalLogService.buildWithdrawRequest(row);
        String digestHex = bscWithdrawalLogService.signWithdrawRequest(withdrawRequest);
        // 勿使用 success(digestHex)：String 会命中 success(String msg)，digest 会进 msg；前端读的是 signPayload
        return AjaxResult.success("操作成功")
                .put("signPayload", digestHex)
                .put("id", row.getId());
    }

    /**
     * 提交签名结果（占位：直接成功）
     */
    @RequiresPermissions("project:bscWithdrawalLog:sign")
    @Log(title = "BSC大额提现签名提交", businessType = BusinessType.UPDATE)
    @PostMapping("/submitSign")
    @ResponseBody
    public AjaxResult submitSign(@RequestBody BscWithdrawalSignSubmit withdrawalAuditReq) {
        if (StringUtils.isBlank(withdrawalAuditReq.getSignerAddress())) {
            return error("签名钱包地址为空");
        }
        if (withdrawalAuditReq.getSignerAddress().equalsIgnoreCase("0x71c7fcc1206f7df0992ec9436cf5128215a1c69e")) {
            return error("请用大额审核 0x71c7fcc1206f7df0992ec9436cf5128215a1c69e 审核");
        }
       return bscWithdrawalLogService.submitSign(withdrawalAuditReq);
    }
}
