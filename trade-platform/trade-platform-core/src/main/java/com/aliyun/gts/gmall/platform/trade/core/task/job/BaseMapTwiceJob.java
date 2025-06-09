package com.aliyun.gts.gmall.platform.trade.core.task.job;

import com.alibaba.schedulerx.worker.domain.JobContext;
import com.alibaba.schedulerx.worker.processor.MapJobProcessor;
import com.alibaba.schedulerx.worker.processor.ProcessResult;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils;
import com.aliyun.gts.gmall.platform.trade.core.util.ParamUtils.Params;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public abstract class BaseMapTwiceJob<T1, T2> extends MapJobProcessor {

    private static final String SUB_NAME_1 = "subTask1";
    private static final String SUB_NAME_2 = "subTask2";

    protected static final String DB_TYPE_POLAR_DB = "PolarDB";

    /**
     * 数据库类型，PolarDB, PolarDB-X， 默认为PolarDB
     */
    @Value("${spring.datasource.dbType:PolarDB-X}")
    protected String dbType;

    @Override
    public ProcessResult process(JobContext context) throws Exception {
        Params params = ParamUtils.parse(context.getJobParameters());
        if (isRootTask(context)) {
            List<T1> tasks = doMainTask(params);
            if (CollectionUtils.isNotEmpty(tasks)) {
                return map(tasks, SUB_NAME_1);
            }
        } else if (SUB_NAME_1.equals(context.getTaskName())) {
            T1 data = (T1) context.getTask();
            List<T2> tasks = doSubTask1(data, params);
            if (CollectionUtils.isNotEmpty(tasks)) {
                return map(tasks, SUB_NAME_2);
            }
        } else if (SUB_NAME_2.equals(context.getTaskName())) {
            T2 data = (T2) context.getTask();
            doSubTask2(data, params);
        } else {
            return new ProcessResult(false);
        }
        return new ProcessResult(true);
    }

    protected abstract List<T1> doMainTask(Params params);

    protected abstract List<T2> doSubTask1(T1 data, Params params);

    protected abstract void doSubTask2(T2 data, Params params);

}
