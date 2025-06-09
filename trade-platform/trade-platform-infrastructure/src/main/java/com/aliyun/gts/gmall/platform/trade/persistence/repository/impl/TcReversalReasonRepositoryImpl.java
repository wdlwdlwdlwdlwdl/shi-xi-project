package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalReasonRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcReversalReasonMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
public class TcReversalReasonRepositoryImpl implements TcReversalReasonRepository {

    @Autowired
    private TcReversalReasonMapper tcReversalReasonMapper;

    @Override
    public List<TcReversalReasonDO> queryByType(Integer reversalType) {
        LambdaQueryWrapper<TcReversalReasonDO> q = Wrappers.lambdaQuery();
        return tcReversalReasonMapper.selectList(q
                .eq(TcReversalReasonDO::getReversalType, reversalType));
    }

    @Override
    public TcReversalReasonDO queryByCode(Integer reasonCode) {
        LambdaQueryWrapper<TcReversalReasonDO> q = Wrappers.lambdaQuery();
        return tcReversalReasonMapper.selectOne(q
                .eq(TcReversalReasonDO::getReasonCode, reasonCode));
    }

    @Override
    public List<TcReversalReasonDO> batchQueryByCode(Collection<Integer> reasonCodes) {
        if (reasonCodes.isEmpty()) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<TcReversalReasonDO> q = Wrappers.lambdaQuery();
        return tcReversalReasonMapper.selectList(q
                .in(TcReversalReasonDO::getReasonCode, reasonCodes));
    }
}
