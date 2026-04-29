package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ActivityConfigMapper;
import com.ruoyi.system.domain.ActivityConfig;
import com.ruoyi.common.core.text.Convert;

/**
 * 活动分类配置Service业务层处理
 * 
 * @author HayDen
 * @date 2025-07-04
 */
@Service
public class ActivityConfigService
{
    @Autowired
    private ActivityConfigMapper activityConfigMapper;

    /**
     * 查询活动分类配置
     * 
     * @param id 活动分类配置ID
     * @return 活动分类配置
     */
    public ActivityConfig selectActivityConfigById(Integer id)
    {
        return activityConfigMapper.selectActivityConfigById(id);
    }

    /**
     * 查询活动分类配置列表
     * 
     * @param activityConfig 活动分类配置
     * @return 活动分类配置
     */
    public List<ActivityConfig> selectActivityConfigList(ActivityConfig activityConfig)
    {
        List<ActivityConfig> activityConfigList = activityConfigMapper.selectActivityConfigList(activityConfig);
        return activityConfigList;
    }

    /**
     * 查询活动分类配置对象
     *
     * @param activityConfig 活动分类配置
     * @return 活动分类配置
     */
    public ActivityConfig findActivityConfig(ActivityConfig activityConfig)
    {
        activityConfig = activityConfigMapper.findActivityConfig(activityConfig);
        return activityConfig;
    }

    /**
     * 修改或者添加活动分类配置
     * 
     * @param activityConfig 活动分类配置
     * @return 结果
     */
    public int updateOrAddActivityConfig(ActivityConfig activityConfig) {
        if (activityConfig.getId() != null){
                    activityConfig.setUpdateTime(DateUtils.getNowDate());
            return activityConfigMapper.updateActivityConfig(activityConfig);
        }else{
            activityConfig.setCreateTime(DateUtils.getNowDate());
                return activityConfigMapper.insertActivityConfig(activityConfig);
        }
    }

    /**
     * 删除活动分类配置对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteActivityConfigByIds(String ids)
    {
        return activityConfigMapper.deleteActivityConfigByIds(Convert.toStrArray(ids));
    }
}
