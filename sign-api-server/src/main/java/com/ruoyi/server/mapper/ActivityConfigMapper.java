package com.ruoyi.server.mapper;

import java.util.List;

import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.server.domain.ActivityConfig;

/**
 * 活动分类配置Mapper接口
 * 
 * @author HayDen
 * @date 2025-07-04
 */
public interface ActivityConfigMapper 
{
    /**
     * 查询活动分类配置
     * 
     * @param id 活动分类配置ID
     * @return 活动分类配置
     */
    @DataSource(value = DataSourceType.SLAVE)
    ActivityConfig selectActivityConfigById(Integer id);

    /**
     * 查询活动分类配置列表
     * 
     * @param activityConfig 活动分类配置
     * @return 活动分类配置集合
     */
    @DataSource(value = DataSourceType.SLAVE)
    List<ActivityConfig> selectActivityConfigList(ActivityConfig activityConfig);

    /**
     * 查询活动分类配置对象
     *
     * @param activityConfig 活动分类配置
     * @return 活动分类配置
     */
    @DataSource(value = DataSourceType.SLAVE)
    ActivityConfig findActivityConfig(ActivityConfig activityConfig);

    /**
     * 新增活动分类配置
     * 
     * @param activityConfig 活动分类配置
     * @return 结果
     */
    int insertActivityConfig(ActivityConfig activityConfig);

    /**
     * 修改活动分类配置
     * 
     * @param activityConfig 活动分类配置
     * @return 结果
     */
    int updateActivityConfig(ActivityConfig activityConfig);

    /**
     * 批量删除活动分类配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteActivityConfigByIds(String[] ids);
}
