package com.aliyun.gts.gmall.platform.trade.core.task.job;

import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils.Params;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.common.DbTable;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTaskId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskExecuteRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.google.common.collect.Lists;
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
public class CleanTaskJob extends BaseMapTwiceJob<DbTable, List<TcAsyncTaskDO>> {
    private static final long ONE_DAY_MS = 24 * 3600 * 1000;

    @Autowired
    private TcAsyncTaskRepository tcAsyncTaskRepository;
    @Autowired
    private TcAsyncTaskExecuteRepository tcAsyncTaskExecuteRepository;

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
            List<TcAsyncTaskDO> batch = tcAsyncTaskRepository.queryList(dbTable,
                    null, new Date(successEndTime),
                    null, successStatus, batchSize, fromTaskId);
            if (batch.isEmpty()) {
                break;
            }
            tasks.add(batch);
            fromTaskId = batch.get(batch.size() - 1).getId();
        }

        // 2. 清理 失败任务 (可保留更长时间)
        fromTaskId = 0L;
        for (int i = 0; i < maxBatch; i++) {
            List<TcAsyncTaskDO> batch = tcAsyncTaskRepository.queryList(dbTable,
                    null, new Date(failEndTime),
                    null, failStatus, batchSize, fromTaskId);
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
        List<ScheduleTaskId> ids = new ArrayList<>();
        for (TcAsyncTaskDO task : data) {
            tcAsyncTaskExecuteRepository.deleteByTaskId(task.getId());
            ids.add(new ScheduleTaskId(task.getId(), task.getPrimaryOrderId()));
        }
        tcAsyncTaskRepository.deleteByIds(ids);
    }
}
