package com.aliyun.gts.gmall.platform.trade.core.task.xxljob;

import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.platform.trade.core.convertor.TaskConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask.ExecuteResult;
import com.aliyun.gts.gmall.platform.trade.core.task.TaskRegister;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils.Params;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * 自动取消订单任务
 */
@Component
public class OrderCancelTaskXxlJob extends BaseMapTwiceXxlJob {

    @Autowired
    private TcAsyncTaskRepository tcAsyncTaskRepository;

    @Autowired
    private TaskConverter taskConverter;

    @Autowired
    private TaskRegister taskRegister;

    @Autowired
    private OrderTaskService orderTaskService;

    @Autowired
    private I18NConfig i18NConfig;

    @XxlJob(value = "orderCancelTaskXxlJob")
    public ReturnT<String> orderCancelTaskXxlJob(String param) {
        LocaleContextHolder.setLocale(new Locale(i18NConfig.getDefaultLang()));
        Params params = ParamUtils.parse(XxlJobHelper.getJobParam());
        long startTime = params.getLong("startTime", 0L);
        long endTime = params.getLong("endTime", System.currentTimeMillis());
        int maxExecuteCount = params.getInt("maxExecuteCount", 20);
        int batchSize = params.getInt("batchSize", 10);
        int maxBatch = params.getInt("maxBatch", 1000);
        List<Integer> taskStatus = Arrays.asList(TcAsyncTaskDO.STATUS_WAIT, TcAsyncTaskDO.STATUS_ERROR);
        for (int i = 0; i < maxBatch; i++) {
            List<TcAsyncTaskDO> batch = tcAsyncTaskRepository.queryTaskList(
                new Date(startTime),
                new Date(endTime),
                maxExecuteCount,
                taskStatus,
                batchSize
            );
            if (batch.isEmpty()) {
                break;
            }
            for (TcAsyncTaskDO task : batch) {
                executeTask(task);
            }
        }
        return ReturnT.SUCCESS;
    }

    /**
     * 执行任务
     * @param task
     */
    private void executeTask(TcAsyncTaskDO task) {
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
