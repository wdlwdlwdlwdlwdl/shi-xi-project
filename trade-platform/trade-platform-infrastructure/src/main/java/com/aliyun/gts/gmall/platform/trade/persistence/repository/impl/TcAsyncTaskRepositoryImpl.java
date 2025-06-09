package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.common.DbTable;
import com.aliyun.gts.gmall.platform.trade.domain.entity.common.ShowNodeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTaskId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.DbTableMapper;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcAsyncTaskMapper;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "tcAsyncTaskRepository",
        havingValue = "default", matchIfMissing = true)
public class TcAsyncTaskRepositoryImpl implements TcAsyncTaskRepository {
    private static final String TABLE_NAME = TcAsyncTaskDO.class.getAnnotation(TableName.class).value();
    private static final Pattern TABLE_PHY_NAME = Pattern.compile(TABLE_NAME + "_[a-zA-Z0-9]+_\\d+");

    @Autowired
    private TcAsyncTaskMapper tcAsyncTaskMapper;
    @Autowired
    private DbTableMapper dbTableMapper;


    @Override
    public void create(TcAsyncTaskDO task) {
        Date d = new Date();
        task.setGmtCreate(d);
        task.setGmtModified(d);
        task.setVersion(1L);
        tcAsyncTaskMapper.insert(task);
    }

    @Override
    public List<TcAsyncTaskDO> queryList(DbTable dbTable,
                                         Date scheduleTimeStart, Date scheduleTimeEnd,
                                         Integer maxExecuteCount,
                                         List<Integer> taskStatus,
                                         int pageSize, long fromTaskId) {
        String hint = String.format(
                "/*+TDDL:scan('%s', real_table=('%s'), node='%s')*/",
                TABLE_NAME, dbTable.getTableName(), dbTable.getDbName());
        QueryWrapper<TcAsyncTaskDO> q = new QueryWrapper<>();
        q.select(hint + " *")
                .gt("id", fromTaskId)
                .orderByAsc("id")
                .last("limit " + pageSize);
        if (CollectionUtils.isNotEmpty(taskStatus)) {
            q.in("task_status", taskStatus);
        }
        if (maxExecuteCount != null) {
            q.lt("execute_count", maxExecuteCount);
        }
        if (scheduleTimeStart != null) {
            q.ge("schedule_time", scheduleTimeStart);
        }
        if (scheduleTimeEnd != null) {
            q.le("schedule_time", scheduleTimeEnd);
        }
        return tcAsyncTaskMapper.selectList(q);
    }

    @Override
    public List<DbTable> queryTaskDbTables() {
        List<DbTable> result = new ArrayList<>();
        List<ShowNodeDO> nodes = dbTableMapper.showNode();
        if (CollectionUtils.isEmpty(nodes)) {
            return result;
        }
        for (ShowNodeDO node : nodes) {
            String dbName = node.getName();
            List<String> tables = dbTableMapper.selectTable(dbName);
            if (CollectionUtils.isEmpty(tables)) {
                continue;
            }
            for (String tableName : tables) {
                Matcher matcher = TABLE_PHY_NAME.matcher(tableName);
                if (matcher.matches()) {
                    result.add(new DbTable(dbName, tableName));
                }
            }
        }
        return result;
    }

    @Override
    public boolean updateByIdVersion(TcAsyncTaskDO task) {
        long version = task.getVersion();
        long newVersion = NumUtils.getNullZero(version) + 1;

        task.setGmtModified(new Date());
        task.setVersion(newVersion);

        LambdaQueryWrapper<TcAsyncTaskDO> q = Wrappers.lambdaQuery();
        int i = tcAsyncTaskMapper.update(task, q
                .eq(TcAsyncTaskDO::getId, task.getId())
                .eq(TcAsyncTaskDO::getPrimaryOrderId, task.getPrimaryOrderId())
                .eq(TcAsyncTaskDO::getVersion, version));
        return i > 0;

    }

    @Override
    public void deleteByIds(List<ScheduleTaskId> ids) {
        List<Long> primaryOrderIds = new ArrayList<>();
        List<Long> taskIds = new ArrayList<>();
        for (ScheduleTaskId id : ids) {
            primaryOrderIds.add(id.getPrimaryOrderId());
            taskIds.add(id.getTaskId());
        }

        LambdaUpdateWrapper<TcAsyncTaskDO> d = Wrappers.lambdaUpdate();
        tcAsyncTaskMapper.delete(d
                .in(TcAsyncTaskDO::getPrimaryOrderId, primaryOrderIds)
                .in(TcAsyncTaskDO::getId, taskIds));
    }

    @Override
    public List<TcAsyncTaskDO> queryByOrder(Long primaryOrderId, List<String> taskTypes) {
        LambdaQueryWrapper<TcAsyncTaskDO> tcAsyncTaskDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        return tcAsyncTaskMapper.selectList(
            tcAsyncTaskDOLambdaQueryWrapper
            .eq(TcAsyncTaskDO::getPrimaryOrderId, primaryOrderId)
            .in(TcAsyncTaskDO::getTaskType, taskTypes)
        );
    }

    @Override
    public List<TcAsyncTaskDO> queryTaskList(Date scheduleTimeStart, Date scheduleTimeEnd,
                                         Integer maxExecuteCount, List<Integer> taskStatus,
                                         int pageSize) {
        QueryWrapper<TcAsyncTaskDO> queryWrapper = new QueryWrapper<>();
        if (CollectionUtils.isNotEmpty(taskStatus)) {
            queryWrapper.in("task_status", taskStatus);
        }
        if (maxExecuteCount != null) {
            queryWrapper.lt("execute_count", maxExecuteCount);
        }
        if (scheduleTimeStart != null) {
            queryWrapper.ge("schedule_time", scheduleTimeStart);
        }
        if (scheduleTimeEnd != null) {
            queryWrapper.le("schedule_time", scheduleTimeEnd);
        }
        queryWrapper.orderByAsc("id")
                .last("limit " + pageSize);
        return tcAsyncTaskMapper.selectList(queryWrapper);
    }
}
