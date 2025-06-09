package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskExecuteDO;

public interface TcAsyncTaskExecuteRepository {

    void create(TcAsyncTaskExecuteDO execute);

    void deleteByTaskId(Long taskId);
}
