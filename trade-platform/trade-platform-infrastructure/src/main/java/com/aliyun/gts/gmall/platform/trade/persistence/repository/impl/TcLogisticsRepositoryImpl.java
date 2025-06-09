package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcLogisticsRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcLogisticsMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TcLogisticsRepositoryImpl implements TcLogisticsRepository {

    @Autowired
    private TcLogisticsMapper tcLogisticsMapper;

    @Override
    public TcLogisticsDO queryTcLogistics(long id) {
        return tcLogisticsMapper.selectById(id);
    }

    @Override
    public TcLogisticsDO create(TcLogisticsDO tcLogisticsDO) {
        tcLogisticsMapper.insert(tcLogisticsDO);
        Long id = tcLogisticsDO.getId();

        if (id != null) {
            return tcLogisticsMapper.selectById(id);
        }

        return null;
    }

    @Override
    public void create(List<TcLogisticsDO> tcLogisticsDOS){
        tcLogisticsMapper.batchInsert(tcLogisticsDOS);
    }

    @Override
    public List<TcLogisticsDO> queryByPrimaryId(Long pOrderId, Long pReversalId) {
        LambdaQueryWrapper<TcLogisticsDO> query = new LambdaQueryWrapper<>();
        query.eq(TcLogisticsDO::getPrimaryOrderId, pOrderId);
        if(pReversalId == null) {
            query.isNull(TcLogisticsDO::getPrimaryReversalId);
        } else {
            query.eq(TcLogisticsDO::getPrimaryReversalId, pReversalId);
        }
        return tcLogisticsMapper.selectList(query);
    }

    @Override
    public TcLogisticsDO queryLogisticsByPrimaryId(Long pOrderId, Long pReversalId) {
        LambdaQueryWrapper<TcLogisticsDO> query = new LambdaQueryWrapper<>();
        query.eq(TcLogisticsDO::getPrimaryOrderId, pOrderId);
        if(pReversalId == null) {
            query.isNull(TcLogisticsDO::getPrimaryReversalId);
        } else {
            query.eq(TcLogisticsDO::getPrimaryReversalId, pReversalId);
        }
        return tcLogisticsMapper.selectOne(query);
    }

    @Override
    public TcLogisticsDO update(TcLogisticsDO tcLogisticsDO) {
        if (tcLogisticsDO.getId() == null) {
            return null;
        }
        int cnt = tcLogisticsMapper.updateById(tcLogisticsDO);
        if (cnt > 0) {
            return tcLogisticsMapper.selectById(tcLogisticsDO.getId());
        }
        return null;
    }

    @Override
    public int delete(Long id) {
        return tcLogisticsMapper.deleteById(id);
    }

}
