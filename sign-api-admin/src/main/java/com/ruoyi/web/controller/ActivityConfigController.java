package com.ruoyi.web.controller;

import com.ruoyi.common.utils.StringUtils;
import java.util.List;
import com.ruoyi.common.core.domain.entity.SysUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.ActivityConfig;
import com.ruoyi.system.service.ActivityConfigService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 活动分类配置Controller
 * 
 * @author HayDen
 * @date 2025-07-04
 */
@Controller
@RequestMapping("/project/activityConfig")
public class ActivityConfigController extends BaseController
{
    private String prefix = "project/activityConfig";

    @Autowired
    private ActivityConfigService activityConfigService;

    @RequiresPermissions("project:activityConfig:view")
    @GetMapping()
    public String activityConfig()
    {
        return prefix + "/activityConfigList";
    }

    /**
     * 查询活动分类配置列表
     */
    @RequiresPermissions("project:activityConfig:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ActivityConfig activityConfig)
    {
        startPage();
        activityConfig.setFlag("1");
        List<ActivityConfig> list = activityConfigService.selectActivityConfigList(activityConfig);
        return getDataTable(list);
    }

    /**
     * 查询活动分类配置对象
     */
    @RequiresPermissions("project:activityConfig:activityConfig")
    @PostMapping("/activityConfig")
    @ResponseBody
    public ActivityConfig findActivityConfig(ActivityConfig activityConfig)
    {
        activityConfig = activityConfigService.findActivityConfig(activityConfig);
        return activityConfig;
    }

    /**
     * 导出活动分类配置列表
     */
    @RequiresPermissions("project:activityConfig:export")
    @Log(title = "活动分类配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(ActivityConfig activityConfig)
    {
        List<ActivityConfig> list = activityConfigService.selectActivityConfigList(activityConfig);
        ExcelUtil<ActivityConfig> util = new ExcelUtil<ActivityConfig>(ActivityConfig.class);
        return util.exportExcel(list, "活动分类配置");
    }

    /**
     * 新增活动分类配置
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/activityConfigAdd";
    }
    /**
     * 新增活动分类配置
     */
    @GetMapping(value = { "/add/{id}", "/add/" })
    public String add(@PathVariable(value = "id", required = false) Integer id, ModelMap mmap)
    {
        if (StringUtils.isNotNull(id))
        {
            mmap.put("activityConfig", activityConfigService.selectActivityConfigById(id));
        }
        return prefix + "/activityConfigAdd";
    }

    /**
     * 新增保存活动分类配置
     */
    @RequiresPermissions("project:activityConfig:add")
    @Log(title = "活动分类配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ActivityConfig activityConfig)
    {
        SysUser sysUser = getSysUser();
        activityConfig.setCreateBy(sysUser.getUserName());
        return toAjax(activityConfigService.updateOrAddActivityConfig(activityConfig));
    }

    /**
     * 修改活动分类配置
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap)
    {
        ActivityConfig activityConfig = activityConfigService.selectActivityConfigById(id);
        mmap.put("activityConfig", activityConfig);
        return prefix + "/activityConfigEdit";
    }

    /**
     * 修改保存活动分类配置
     */
    @RequiresPermissions("project:activityConfig:edit")
    @Log(title = "活动分类配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(ActivityConfig activityConfig)
    {
        SysUser sysUser = getSysUser();
        activityConfig.setUpdateBy(sysUser.getUserName());
        return toAjax(activityConfigService.updateOrAddActivityConfig(activityConfig));
    }


    /**
     * 删除活动分类配置
     */
    @RequiresPermissions("project:activityConfig:remove")
    @Log(title = "活动分类配置", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        Integer id = Integer.parseInt(ids);
        ActivityConfig activityConfig = activityConfigService.selectActivityConfigById(id);
        if(activityConfig == null){
            return error("数据不存在");
        }
        activityConfig.setFlag("0");
        activityConfigService.updateOrAddActivityConfig(activityConfig);
        return toAjax(1);
    }

}
