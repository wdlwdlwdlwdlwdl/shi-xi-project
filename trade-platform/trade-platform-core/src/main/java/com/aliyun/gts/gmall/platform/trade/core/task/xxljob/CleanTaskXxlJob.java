package com.aliyun.gts.gmall.platform.trade.core.task.xxljob;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.schedulerx.worker.domain.JobContext;
import com.alibaba.schedulerx.worker.processor.ProcessResult;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils.Params;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.common.DbTable;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTaskId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskExecuteRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.google.common.collect.Lists;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 清理历史任务
 */
@Component
public class CleanTaskXxlJob extends BaseMapTwiceXxlJob {
    private static final long ONE_DAY_MS = 24 * 3600 * 1000;

    @Autowired
    private TcAsyncTaskRepository tcAsyncTaskRepository;
    @Autowired
    private TcAsyncTaskExecuteRepository tcAsyncTaskExecuteRepository;

    /**
     * 原始入参 json
     * {
     * 	"attempt": 0,
     * 	"content": "{\"className\":\"com.aliyun.gts.gmall.platform.trade.core.task.job.CleanTaskJob\"}",
     * 	"dataTime": "2024-07-19T14:47:40.039+08:00",
     * 	"executeMode": "batch",
     * 	"groupId": "trade-center-dev",
     * 	"instanceMasterActorPath": "akka.tcp://192168001069_1_61040@192.168.1.69:38873/user/task_routing",
     * 	"instanceParameters": "",
     * 	"jobId": 26627924,
     * 	"jobInstanceId": 69562424982,
     * 	"jobName": "CleanTaskJob",
     * 	"jobParameters": "",
     * 	"jobType": "java",
     * 	"maxAttempt": 0,
     * 	"scheduleTime": "2024-07-19T14:47:40.039+08:00",
     * 	"serialNum": 0,
     * 	"task": "MAP_TASK_ROOT",
     * 	"taskAttempt": 0,
     * 	"taskAttemptInterval": 0,
     * 	"taskId": 0,
     * 	"taskMasterActorPath": "akka.tcp://192168001069_1_61040@192.168.1.69:38873/user/task_routing",
     * 	"taskMaxAttempt": 0,
     * 	"taskName": "MAP_TASK_ROOT",
     * 	"uniqueId": "26627924_69562424982_0",
     * 	"upstreamData": [],
     * 	"user": "chaoyin(chaoyin)"
     * }
     * @param param
     * @return
     */
    @XxlJob(value = "shardingCleanTaskXxlJob")
    public ReturnT<String> shardingCleanTaskXxlJob(String param) {
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
     * 查询所有的分库和分表
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
        int batchSize = params.getInt("batchSize", 10);
        int maxBatch = params.getInt("maxBatch", 10000);
        int cleanSuccessDays = params.getInt("cleanSuccessDays", 7);
        int cleanFailDays = params.getInt("cleanFailDays", 90);

        List<List<TcAsyncTaskDO>> tasks = new ArrayList<>();
        long successEndTime = System.currentTimeMillis() - ONE_DAY_MS * cleanSuccessDays;
        long failEndTime = System.currentTimeMillis() - ONE_DAY_MS * cleanFailDays;
        List<Integer> successStatus = Arrays.asList(TcAsyncTaskDO.STATUS_FINISH, TcAsyncTaskDO.STATUS_DELETE);
        List<Integer> failStatus = List.of(TcAsyncTaskDO.STATUS_ERROR);

        // 1. 清理 成功的任务 (+逻辑删除的任务)
        long fromTaskId = 0L;
        for (int i = 0; i < maxBatch; i++) {
            List<TcAsyncTaskDO> batch = tcAsyncTaskRepository.queryList(
                dbTable,
                null,
                new Date(successEndTime),
                null,
                successStatus,
                batchSize,
                fromTaskId
            );
            if (batch.isEmpty()) {
                break;
            }
            tasks.add(batch);
            fromTaskId = batch.get(batch.size() - 1).getId();
        }

        // 2. 清理 失败任务 (可保留更长时间)
        fromTaskId = 0L;
        for (int i = 0; i < maxBatch; i++) {
            List<TcAsyncTaskDO> batch = tcAsyncTaskRepository.queryList(
                dbTable,
                null,
                new Date(failEndTime),
                null,
                failStatus,
                batchSize,
                fromTaskId
            );
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
        List<ScheduleTaskId> ids = new ArrayList<>();
        for (TcAsyncTaskDO task : data) {
            tcAsyncTaskExecuteRepository.deleteByTaskId(task.getId());
            ids.add(new ScheduleTaskId(task.getId(), task.getPrimaryOrderId()));
        }
        tcAsyncTaskRepository.deleteByIds(ids);
    }



}
