package com.aliyun.gts.gmall.platform.trade.core.task.xxljob;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.platform.trade.core.convertor.TaskConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask.ExecuteResult;
import com.aliyun.gts.gmall.platform.trade.core.task.TaskRegister;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils.Params;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.common.DbTable;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.google.common.collect.Lists;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
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
public class OrderTaskXxlJob extends BaseMapTwiceXxlJob {

    @Autowired
    private TcAsyncTaskRepository tcAsyncTaskRepository;
    @Autowired
    private TaskConverter taskConverter;
    @Autowired
    private TaskRegister taskRegister;
    @Autowired
    private OrderTaskService orderTaskService;

    /** 原始入参 json
     * {
     * 	"attempt": 0,
     * 	"content": "{\"className\":\"com.aliyun.gts.gmall.platform.trade.core.task.job.OrderTaskJob\"}",
     * 	"dataTime": "2024-07-19T14:41:05.157+08:00",
     * 	"executeMode": "batch",
     * 	"groupId": "trade-center-dev",
     * 	"instanceMasterActorPath": "akka.tcp://192168001069_1_61040@192.168.1.69:38873/user/task_routing",
     * 	"instanceParameters": "",
     * 	"jobId": 26627923,
     * 	"jobInstanceId": 69562359252,
     * 	"jobName": "OrderTaskJob",
     * 	"jobParameters": "{\n\"batchSize\": 10\n}",
     * 	"jobType": "java",
     * 	"maxAttempt": 0,
     * 	"scheduleTime": "2024-07-19T14:41:05.157+08:00",
     * 	"serialNum": 0,
     * 	"task": "MAP_TASK_ROOT",
     * 	"taskAttempt": 0,
     * 	"taskAttemptInterval": 0,
     * 	"taskId": 0,
     * 	"taskMasterActorPath": "akka.tcp://192168001069_1_61040@192.168.1.69:38873/user/task_routing",
     * 	"taskMaxAttempt": 0,
     * 	"taskName": "MAP_TASK_ROOT",
     * 	"uniqueId": "26627923_69562359252_0",
     * 	"upstreamData": [],
     * 	"user": "chaoyin(chaoyin)"
     * }
     * @param param
     * @return
     */

    @XxlJob(value = "shardingOrderTaskXxlJob")
    public ReturnT<String> shardingOrderTaskXxlJob(String param) {
        JSONObject jsonObject = JSONObject.parseObject(param);
        String jobParameters = jsonObject.getString("jobParameters");
        String taskName = jsonObject.getString("taskName");
        Object task = jsonObject.getString("task");
        Params params = ParamUtils.parse(jobParameters);
        if (isRootTask(taskName)) {
            List<DbTable> tasks = doMainTask(params);
            if (CollectionUtils.isNotEmpty(tasks)) {
                return shardingCleanTask(tasks, SUB_NAME_1);
            }
        } else if (SUB_NAME_1.equals(taskName)) {
            DbTable data = (DbTable) task;
            List<List<TcAsyncTaskDO>> tasks = doSubTask1(data, params);
            if (CollectionUtils.isNotEmpty(tasks)) {
                return shardingCleanTask(tasks, SUB_NAME_2);
            }
        } else if (SUB_NAME_2.equals(taskName)) {
            List<TcAsyncTaskDO> data = (List<TcAsyncTaskDO>) task;
            doSubTask2(data, params);
        } else {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }



    /**
     * 主任务: 按 DB+table 拆分任务分发
     */

    protected List<DbTable> doMainTask(Params params) {
        if (StringUtils.equals(DB_TYPE_POLAR_DB, dbType)) {
            return Lists.newArrayList(new DbTable());
        }
        return tcAsyncTaskRepository.queryTaskDbTables();
    }

    /**
     * 子任务1: 查单库单表中的task, 按batchSize 分发
     */

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
