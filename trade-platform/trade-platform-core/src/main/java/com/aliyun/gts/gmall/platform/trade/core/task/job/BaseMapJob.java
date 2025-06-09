package com.aliyun.gts.gmall.platform.trade.core.task.job;

import com.alibaba.schedulerx.worker.domain.JobContext;
import com.alibaba.schedulerx.worker.processor.MapJobProcessor;
import com.alibaba.schedulerx.worker.processor.ProcessResult;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils.Params;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public abstract class BaseMapJob<T> extends MapJobProcessor {
    private static final String SUB_NAME = "subTask";

    @Override
    public ProcessResult process(JobContext context) throws Exception {
        Params params = ParamUtils.parse(context.getJobParameters());
        if (isRootTask(context)) {
            List<T> tasks = doMainTask(params);
            if (CollectionUtils.isNotEmpty(tasks)) {
                return map(tasks, SUB_NAME);
            }
        } else if (SUB_NAME.equals(context.getTaskName())) {
            T data = (T) context.getTask();
            doSubTask(data, params);
        } else {
            return new ProcessResult(false);
        }
        return new ProcessResult(true);
    }

    protected abstract List<T> doMainTask(Params params);

    protected abstract void doSubTask(T data, Params params);
}
