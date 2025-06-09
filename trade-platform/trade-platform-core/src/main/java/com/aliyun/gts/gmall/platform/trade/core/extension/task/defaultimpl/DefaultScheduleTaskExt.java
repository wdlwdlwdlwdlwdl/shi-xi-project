package com.aliyun.gts.gmall.platform.trade.core.extension.task.defaultimpl;

import com.aliyun.gts.gmall.platform.trade.core.extension.task.ScheduleTaskExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultScheduleTaskExt implements ScheduleTaskExt {

    @Override
    public ScheduleTask beforeCreateTask(ScheduleTask task) {
        return task;
    }

    @Override
    public ScheduleTask beforeExecuteTask(ScheduleTask task) {
        return task;
    }
}
