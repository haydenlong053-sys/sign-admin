package com.ruoyi.web.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.server.domain.WithdrawReconcileLog;
import com.ruoyi.server.domain.WithdrawReconcileStat;
import com.ruoyi.server.domain.WithdrawReconcileStatDTO;
import com.ruoyi.server.service.IWithdrawReconcileService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * BSC提现对账记录Controller
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/project/withdrawReconcile")
public class WithdrawReconcileController extends BaseController
{
    private String prefix = "project/withdrawReconcile";

    @Autowired
    private IWithdrawReconcileService withdrawReconcileService;

    @RequiresPermissions("project:withdrawReconcile:view")
    @GetMapping()
    public String withdrawReconcile()
    {
        return prefix + "/withdrawReconcileList";
    }

    /**
     * 查询BSC提现对账记录列表
     */
    @RequiresPermissions("project:withdrawReconcile:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(WithdrawReconcileLog withdrawReconcileLog)
    {
        startPage();
        List<WithdrawReconcileLog> list = withdrawReconcileService.selectWithdrawReconcileLogList(withdrawReconcileLog);
        return getDataTable(list);
    }

    /**
     * 获取出账统计数据（按币种+时间维度：总计/昨日/当日）
     */
    @RequiresPermissions("project:withdrawReconcile:view")
    @PostMapping("/stats/outbound")
    @ResponseBody
    public AjaxResult getOutboundStats(String startDate, String endDate)
    {
        List<WithdrawReconcileStatDTO> stats = withdrawReconcileService.getOutboundStats(startDate, endDate);
        return AjaxResult.success(stats);
    }

    /**
     * 导出BSC提现对账记录列表
     */
    @RequiresPermissions("project:withdrawReconcile:export")
    @Log(title = "BSC提现对账记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(WithdrawReconcileLog withdrawReconcileLog)
    {
        List<WithdrawReconcileLog> list = withdrawReconcileService.selectWithdrawReconcileLogList(withdrawReconcileLog);
        ExcelUtil<WithdrawReconcileLog> util = new ExcelUtil<WithdrawReconcileLog>(WithdrawReconcileLog.class);
        return util.exportExcel(list, "BSC提现对账记录");
    }
}
