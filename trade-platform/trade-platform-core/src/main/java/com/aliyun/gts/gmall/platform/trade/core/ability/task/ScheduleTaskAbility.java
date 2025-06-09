package com.aliyun.gts.gmall.platform.trade.core.ability.task;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.task.ScheduleTaskExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.task.defaultimpl.DefaultScheduleTaskExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@Ability(
        code = "com.aliyun.gts.gmall.platform.trade.core.ability.task.ScheduleTaskAbility",
        fallback = DefaultScheduleTaskExt.class,
        description = "定时任务扩展点"
)
public class ScheduleTaskAbility extends BaseAbility<BizCodeEntity, ScheduleTaskExt> {

    /**
     * 创建定时任务前的拦截处理
     */
    public ScheduleTask beforeCreateTask(ScheduleTask task, List<BizCodeEntity> bizCodes) {
        for (BizCodeEntity bizCode : bizCodes) {
            final ScheduleTask t = task;
            task = executeExt(bizCode,
                    extension -> extension.beforeCreateTask(t),
                    ScheduleTaskExt.class,
                    Reducers.firstOf(Objects::nonNull));
        }
        return task;
    }

    /**
     * 执行定时任务前的拦截处理
     */
    public ScheduleTask beforeExecuteTask(ScheduleTask task, List<BizCodeEntity> bizCodes) {
        for (BizCodeEntity bizCode : bizCodes) {
            final ScheduleTask t = task;
            task = executeExt(bizCode,
                    extension -> extension.beforeExecuteTask(t),
                    ScheduleTaskExt.class,
                    Reducers.firstOf(Objects::nonNull));
        }
        return task;
    }
}
