package com.aliyun.gts.gmall.platform.trade.core.extension.task;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;

public interface ScheduleTaskExt extends IExtensionPoints {

    /**
     * 创建定时任务前的拦截处理
     */
    ScheduleTask beforeCreateTask(ScheduleTask task);

    /**
     * 执行定时任务前的拦截处理
     */
    ScheduleTask beforeExecuteTask(ScheduleTask task);
}
