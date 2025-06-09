package com.aliyun.gts.gmall.center.trade.presale.ext;

import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonScheduleTaskExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.task.ScheduleTaskExt;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.SystemConfirmOrderTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 电子凭证 无 "自动确认收货任务"
 */
@Slf4j
@Extension(points = {ScheduleTaskExt.class})
public class EvoucherScheduleTaskExt extends CommonScheduleTaskExt {

    @Autowired
    private SystemConfirmOrderTask systemConfirmOrderTask;

    @Override
    public ScheduleTask beforeCreateTask(ScheduleTask task) {
        // 自动确认收货任务, return null
        if (systemConfirmOrderTask.getTaskType().equals(task.getType())) {
            return null;
        }
        return super.beforeCreateTask(task);
    }
}
