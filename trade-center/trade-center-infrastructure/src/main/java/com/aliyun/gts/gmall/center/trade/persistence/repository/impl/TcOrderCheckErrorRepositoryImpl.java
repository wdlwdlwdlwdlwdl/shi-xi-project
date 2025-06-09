package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.OrderCheckErrorParam;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcOrderCheckErrorDO;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcOrderCheckErrorRepository;
import com.aliyun.gts.gmall.center.trade.persistence.mapper.TcOrderCheckErrorMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TcOrderCheckErrorRepositoryImpl implements TcOrderCheckErrorRepository {

    @Autowired
    private TcOrderCheckErrorMapper tcOrderCheckErrorMapper;

    @Override
    public TcOrderCheckErrorDO queryTcOrderCheckError(long id) {
        return tcOrderCheckErrorMapper.selectById(id);
    }

    @Override
    public List<TcOrderCheckErrorDO> queryByParam(OrderCheckErrorParam orderCheckErrorParam) {

        if (orderCheckErrorParam.getPrimaryOrderId() == null || orderCheckErrorParam.getCheckType() == null) {
            throw new IllegalArgumentException("primaryOrderId or chekType is null");
        }
        LambdaQueryWrapper<TcOrderCheckErrorDO> wrapper = Wrappers.lambdaQuery();
        if (orderCheckErrorParam.getPrimaryOrderId() != null) {
            wrapper.eq(TcOrderCheckErrorDO::getPrimaryOrderId, orderCheckErrorParam.getPrimaryOrderId());
        }

        if (orderCheckErrorParam.getCheckType() != null) {
            wrapper.eq(TcOrderCheckErrorDO::getCheckType, orderCheckErrorParam.getCheckType());
        }

        if (orderCheckErrorParam.getPrimaryReversalId() != null) {
            wrapper.eq(TcOrderCheckErrorDO::getPrimaryReversalId, orderCheckErrorParam.getPrimaryReversalId());
        }

        return tcOrderCheckErrorMapper.selectList(wrapper);
    }

    @Override
    public TcOrderCheckErrorDO create(TcOrderCheckErrorDO tcOrderCheckErrorDO) {
        tcOrderCheckErrorMapper.insert(tcOrderCheckErrorDO);
        Long id = tcOrderCheckErrorDO.getId();

        if (id != null) {
            return tcOrderCheckErrorMapper.selectById(id);
        }

        return null;
    }

    @Override
    public TcOrderCheckErrorDO update(TcOrderCheckErrorDO tcOrderCheckErrorDO) {
        if (tcOrderCheckErrorDO.getId() == null) {
            return null;
        }

        int cnt = tcOrderCheckErrorMapper.updateById(tcOrderCheckErrorDO);
        if (cnt > 0) {
            return tcOrderCheckErrorMapper.selectById(tcOrderCheckErrorDO.getId());
        }

        return null;
    }

    @Override
    public int delete(Long id) {
        return tcOrderCheckErrorMapper.deleteById(id);
    }

}
