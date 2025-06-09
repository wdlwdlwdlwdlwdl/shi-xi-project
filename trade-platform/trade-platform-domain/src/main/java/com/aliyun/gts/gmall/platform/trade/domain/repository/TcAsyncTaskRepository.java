package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.common.DbTable;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTaskId;

import java.util.Date;
import java.util.List;

public interface TcAsyncTaskRepository {

    void create(TcAsyncTaskDO task);

    /**
     * 扫表查询, 用于执行 / 清理任务
     */
    List<TcAsyncTaskDO> queryList(
        DbTable dbTable,             // 指定分库分表
        Date scheduleTimeStart,
        Date scheduleTimeEnd,        // 执行时间
        Integer maxExecuteCount,     // 最大执行次数
        List<Integer> taskStatus,    // 任务状态
        int pageSize,                // 分页参数
        long fromTaskId
    );

    /**
     * 获取task分库分表信息
     */
    List<DbTable> queryTaskDbTables();


    /**
     * 根据 id、primaryOrderId、version 来更新
     */
    boolean updateByIdVersion(TcAsyncTaskDO task);

    /**
     * 根据 id、primaryOrderId 物理删除
     */
    void deleteByIds(List<ScheduleTaskId> ids);

    /**
     * 根据 订单ID + type 查
     */
    List<TcAsyncTaskDO> queryByOrder(Long primaryOrderId, List<String> taskTypes);


    List<TcAsyncTaskDO> queryTaskList(
        Date scheduleTimeStart,
        Date scheduleTimeEnd,
        Integer maxExecuteCount,
        List<Integer> taskStatus,
        int pageSize
    );
}
