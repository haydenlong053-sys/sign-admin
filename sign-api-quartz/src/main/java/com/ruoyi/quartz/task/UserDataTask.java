package com.ruoyi.quartz.task;

import org.springframework.stereotype.Component;

/**
 * 占位任务：原统计逻辑依赖的业务模块已移除。若 Quartz 库中仍引用 bean {@code userData}，保留此类以免启动报错；
 * 无需调度时请删除库内对应任务定义。
 */
@Component("userData")
public class UserDataTask {

    public void exec() {
        // no-op
    }
}
