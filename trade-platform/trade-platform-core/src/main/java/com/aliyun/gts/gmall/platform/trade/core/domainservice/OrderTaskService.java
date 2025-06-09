package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTaskId;

import java.util.List;

public interface OrderTaskService {

    /**
     * 创建异步调度任务
     */
    List<ScheduleTaskId> createScheduledTask(List<ScheduleTask> tasks);

    /**
     * 删除异步任务, 物理删除, 用于订单创建过程的回滚
     */
    void deleteScheduledTask(List<ScheduleTaskId> ids);

    /**
     * 逻辑删除, 用于更新任务, 先删再重建
     */
    int logicalDeleteByOrder(Long primaryOrderId, String taskType);

    /**
     * 记录执行结果
     */
    default void saveExecuteResult(TcAsyncTaskDO task, boolean success, String resultMessage) {
        saveExecuteResult(task, success, resultMessage, false);
    }

    /**
     * 记录执行结果
     */
    void saveExecuteResult(TcAsyncTaskDO task, boolean success, String resultMessage, boolean later);
}
