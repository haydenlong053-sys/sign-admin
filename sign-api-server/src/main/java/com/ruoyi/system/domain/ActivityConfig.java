package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import lombok.experimental.Accessors;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 活动分类配置对象 activity_config
 *
 * @author HayDen
 * @date 2025-07-04
 */
@Setter
@Getter
@Accessors(chain = true)
public class ActivityConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;


    /**
     * 标记删除，0 / 1
     */
    @Excel(name = "标记删除，0 / 1")
    private String flag;

    /**
     * 活动类别
     */
    @Excel(name = "活动类别")
    private String activityName;

    /**
     * 语言
     */
    @Excel(name = "语言")
    private String lang;

}
