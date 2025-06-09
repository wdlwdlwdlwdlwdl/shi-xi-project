package com.aliyun.gts.gmall.platform.trade.core.task.xxljob;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
public abstract class BaseMapTwiceXxlJob {

    public static final String SUB_NAME_1 = "subTask1";
    public static final String SUB_NAME_2 = "subTask2";

    public static final String DB_TYPE_POLAR_DB = "PolarDB";

    /**
     * 数据库类型，PolarDB, PolarDB-X， 默认为PolarDB
     */
    @Value("${spring.datasource.dbType:PolarDB-X}")
    public String dbType;

    /**
     * 将子任务分布到多台服务器上运行
     * 该方法需研究开发
     * @param tasks
     * @param subName
     * @return
     */
    public ReturnT<String> shardingCleanTask(List<? extends Object> tasks, String subName) {

        return ReturnT.SUCCESS;
    }

    public boolean isRootTask(String taskName) {
        return taskName.equals("MAP_TASK_ROOT");
    }


}
