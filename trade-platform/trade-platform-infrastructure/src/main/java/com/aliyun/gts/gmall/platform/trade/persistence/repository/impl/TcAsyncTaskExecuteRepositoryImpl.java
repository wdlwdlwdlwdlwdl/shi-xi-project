package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskExecuteDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskExecuteRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcAsyncTaskExecuteMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TcAsyncTaskExecuteRepositoryImpl implements TcAsyncTaskExecuteRepository {

    @Autowired
    private TcAsyncTaskExecuteMapper tcAsyncTaskExecuteMapper;

    @Override
    public void create(TcAsyncTaskExecuteDO execute) {
        execute.setGmtCreate(new Date());
        tcAsyncTaskExecuteMapper.insert(execute);
    }

    @Override
    public void deleteByTaskId(Long taskId) {
        LambdaQueryWrapper<TcAsyncTaskExecuteDO> q = Wrappers.lambdaQuery();
        tcAsyncTaskExecuteMapper.delete(q
                .eq(TcAsyncTaskExecuteDO::getTaskId, taskId));
    }
}
