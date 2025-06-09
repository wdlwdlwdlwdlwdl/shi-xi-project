package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.convertor.TaskConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskExecuteDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTaskId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskExecuteRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderTaskServiceImpl implements OrderTaskService {
    private static final int MAX_MSG_SIZE = 2000;

    @Autowired
    private TcAsyncTaskRepository tcAsyncTaskRepository;
    @Autowired
    private TcAsyncTaskExecuteRepository tcAsyncTaskExecuteRepository;
    @Autowired
    private TaskConverter taskConverter;

    @Override
    public List<ScheduleTaskId> createScheduledTask(List<ScheduleTask> tasks) {
        List<ScheduleTaskId> list = new ArrayList<>();
        for (ScheduleTask task : tasks) {
            if (task == null) {
                continue;
            }
            TcAsyncTaskDO t = taskConverter.toTcAsyncTaskDO(task);
            tcAsyncTaskRepository.create(t);
            list.add(new ScheduleTaskId(t.getId(), t.getPrimaryOrderId()));
        }
        return list;
    }

    @Override
    public void deleteScheduledTask(List<ScheduleTaskId> ids) {
        tcAsyncTaskRepository.deleteByIds(ids);
    }

    @Override
    public int logicalDeleteByOrder(Long primaryOrderId, String taskType) {
        int deleted = 0;
        List<TcAsyncTaskDO> tasks = tcAsyncTaskRepository.queryByOrder(primaryOrderId, Lists.newArrayList(taskType));
        if (CollectionUtils.isEmpty(tasks)) {
            return deleted;
        }
        for (TcAsyncTaskDO task : tasks) {
            if (task.getTaskStatus().intValue() == TcAsyncTaskDO.STATUS_DELETE) {
                continue;
            }
            TcAsyncTaskDO up = new TcAsyncTaskDO();
            up.setPrimaryOrderId(task.getPrimaryOrderId());
            up.setId(task.getId());
            up.setVersion(task.getVersion());
            up.setTaskStatus(TcAsyncTaskDO.STATUS_DELETE);
            boolean b = tcAsyncTaskRepository.updateByIdVersion(up);
            if (!b) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
            deleted++;
        }
        return deleted;
    }

    @Override
    @Transactional
    public void saveExecuteResult(TcAsyncTaskDO task, boolean success,
                                  String resultMessage, boolean later) {
        if (resultMessage != null && resultMessage.length() > MAX_MSG_SIZE) {
            resultMessage = resultMessage.substring(0, MAX_MSG_SIZE);
        }

        TcAsyncTaskDO up = new TcAsyncTaskDO();
        up.setId(task.getId());
        up.setPrimaryOrderId(task.getPrimaryOrderId());
        up.setVersion(task.getVersion());
        up.setTaskStatus(success ? TcAsyncTaskDO.STATUS_FINISH : TcAsyncTaskDO.STATUS_ERROR);
        up.setExecuteMessage(resultMessage);
        if (!later) {
            up.setExecuteCount(task.getExecuteCount() + 1);
        }

        TcAsyncTaskExecuteDO execute = new TcAsyncTaskExecuteDO();
        execute.setTaskId(task.getId());
        execute.setSuccess(success);
        execute.setResultMessage(resultMessage);

        tcAsyncTaskExecuteRepository.create(execute);
        tcAsyncTaskRepository.updateByIdVersion(up);
    }
}
