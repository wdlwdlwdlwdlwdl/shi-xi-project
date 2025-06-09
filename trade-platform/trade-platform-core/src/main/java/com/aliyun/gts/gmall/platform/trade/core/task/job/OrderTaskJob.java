package com.aliyun.gts.gmall.platform.trade.core.task.job;

import com.aliyun.gts.gmall.platform.trade.core.convertor.TaskConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask.ExecuteResult;
import com.aliyun.gts.gmall.platform.trade.core.task.TaskRegister;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils.Params;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.common.DbTable;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 执行异步任务
 */
@Component
public class OrderTaskJob extends BaseMapTwiceJob<DbTable, List<TcAsyncTaskDO>> {

    @Autowired
    private TcAsyncTaskRepository tcAsyncTaskRepository;
    @Autowired
    private TaskConverter taskConverter;
    @Autowired
    private TaskRegister taskRegister;
    @Autowired
    private OrderTaskService orderTaskService;

    /**
     * 主任务: 按 DB+table 拆分任务分发
     */
    @Override
    protected List<DbTable> doMainTask(Params params) {
        if (StringUtils.equals(DB_TYPE_POLAR_DB, dbType)) {
            return Lists.newArrayList(new DbTable());
        }
        return tcAsyncTaskRepository.queryTaskDbTables();
    }

    /**
     * 子任务1: 查单库单表中的task, 按batchSize 分发
     */
    @Override
    protected List<List<TcAsyncTaskDO>> doSubTask1(DbTable dbTable, Params params) {
        long startTime = params.getLong("startTime", 0L);
        long endTime = params.getLong("endTime", System.currentTimeMillis());
        int maxExecuteCount = params.getInt("maxExecuteCount", 20);
        int batchSize = params.getInt("batchSize", 10);
        int maxBatch = params.getInt("maxBatch", 10000);

        List<List<TcAsyncTaskDO>> tasks = new ArrayList<>();
        long fromTaskId = 0L;
        List<Integer> taskStatus = Arrays.asList(TcAsyncTaskDO.STATUS_WAIT, TcAsyncTaskDO.STATUS_ERROR);
        for (int i = 0; i < maxBatch; i++) {
            List<TcAsyncTaskDO> batch = tcAsyncTaskRepository.queryList(dbTable,
                    new Date(startTime), new Date(endTime),
                    maxExecuteCount, taskStatus, batchSize, fromTaskId);
            if (batch.isEmpty()) {
                break;
            }
            tasks.add(batch);
            fromTaskId = batch.get(batch.size() - 1).getId();
        }
        return tasks;
    }

    /**
     * 子任务2: 处理每个batch 中每个task
     */
    @Override
    protected void doSubTask2(List<TcAsyncTaskDO> data, Params params) {
        for (TcAsyncTaskDO task : data) {
            doSingleTask(task);
        }
    }

    private void doSingleTask(TcAsyncTaskDO task) {
        ScheduleTask scheduleTask = taskConverter.toScheduleTask(task);
        AbstractScheduledTask executor = taskRegister.getTaskMap().get(scheduleTask.getType());
        if (executor == null) {
            orderTaskService.saveExecuteResult(task, false, "Executor not found");
            return;
        }
        try {
            ExecuteResult result = executor.execute(scheduleTask);
            orderTaskService.saveExecuteResult(task, result.isSuccess(), result.getMessage(), result.isLater());
        } catch (Exception e) {
            String msg = ExceptionUtils.getStackTrace(e);
            orderTaskService.saveExecuteResult(task, false, msg);
        }
    }
}
