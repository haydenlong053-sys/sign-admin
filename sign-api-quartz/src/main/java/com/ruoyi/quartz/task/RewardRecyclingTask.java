package com.ruoyi.quartz.task;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 占位：原奖励回收逻辑依赖的业务模块已移除。
 */
@Component
public class RewardRecyclingTask implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        exec();
    }

    public void exec() {
        // no-op
    }
}
