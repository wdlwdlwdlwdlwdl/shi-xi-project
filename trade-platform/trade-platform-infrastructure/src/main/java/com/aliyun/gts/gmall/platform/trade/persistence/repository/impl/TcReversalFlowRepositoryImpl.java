package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalFlowRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcReversalFlowMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TcReversalFlowRepositoryImpl implements TcReversalFlowRepository {

    @Autowired
    private TcReversalFlowMapper tcReversalFlowMapper;

    @Override
    public void insert(TcReversalFlowDO flow) {
        tcReversalFlowMapper.insert(flow);
    }

    @Override
    public List<TcReversalFlowDO> query(Long primaryReversalId) {
        LambdaQueryWrapper<TcReversalFlowDO> q = Wrappers.lambdaQuery();
        return tcReversalFlowMapper.selectList(q
                .eq(TcReversalFlowDO::getPrimaryReversalId, primaryReversalId)
                .orderByAsc(TcReversalFlowDO::getGmtCreate));
    }

    @Override
    public List<TcReversalFlowDO> batchQuery(List<Long> primaryReversalIds) {
        if (primaryReversalIds.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<TcReversalFlowDO> q = Wrappers.lambdaQuery();
        return tcReversalFlowMapper.selectList(q
                .in(TcReversalFlowDO::getPrimaryReversalId, primaryReversalIds)
                .orderByAsc(TcReversalFlowDO::getGmtCreate));
    }

    @Override
    public TcReversalFlowDO queryReversalFlow(Long primaryReversalId) {
        LambdaQueryWrapper<TcReversalFlowDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TcReversalFlowDO::getPrimaryReversalId, primaryReversalId)
                .orderByAsc(TcReversalFlowDO::getGmtCreate)
                .last("LIMIT 1");
        return tcReversalFlowMapper.selectOne(wrapper);
    }
}
