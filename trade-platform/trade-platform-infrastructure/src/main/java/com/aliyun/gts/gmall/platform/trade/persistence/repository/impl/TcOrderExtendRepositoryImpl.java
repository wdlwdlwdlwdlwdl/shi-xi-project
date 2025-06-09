package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderExtendRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcOrderExtendMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class TcOrderExtendRepositoryImpl implements TcOrderExtendRepository {

    @Autowired
    private TcOrderExtendMapper tcOrderExtendMapper;

    @Override
    public TcOrderExtendDO queryTcOrderExtend(long id) {
        return tcOrderExtendMapper.selectById(id);
    }

    @Override
    public List<TcOrderExtendDO> queryExtendsByPrimaryOrderId(long primaryOrderId) {
        LambdaQueryWrapper<TcOrderExtendDO> q = Wrappers.lambdaQuery();
        return tcOrderExtendMapper.selectList(q.eq(TcOrderExtendDO::getPrimaryOrderId, primaryOrderId));
    }

    @Override
    public List<TcOrderExtendDO> queryExtendsByPrimaryOrderIds(Long custId, List<Long> primaryOrderIds) {
        LambdaQueryWrapper<TcOrderExtendDO> q = Wrappers.lambdaQuery();
        q.eq(TcOrderExtendDO::getCustId, custId);
        q.in(TcOrderExtendDO::getPrimaryOrderId, primaryOrderIds);
        return tcOrderExtendMapper.selectList(q);
    }

    @Override
    public List<TcOrderExtendDO> queryByParams(TcOrderExtendDO params) {
        if (params.getPrimaryOrderId() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        LambdaQueryWrapper<TcOrderExtendDO> q = Wrappers.lambdaQuery();
        q.eq(TcOrderExtendDO::getPrimaryOrderId, params.getPrimaryOrderId());
        if (params.getOrderId() != null) {
            q.eq(TcOrderExtendDO::getOrderId, params.getOrderId());
        }
        if (params.getExtendType() != null) {
            q.eq(TcOrderExtendDO::getExtendType, params.getExtendType());
        }
        if (params.getExtendKey() != null) {
            q.eq(TcOrderExtendDO::getExtendKey, params.getExtendKey());
        }
        return tcOrderExtendMapper.selectList(q);
    }

    @Override
    public void create(TcOrderExtendDO tcOrderExtendDO) {
        tcOrderExtendMapper.insert(tcOrderExtendDO);
    }

    @Override
    public void insertOrUpdate(TcOrderExtendDO ext) {
        tcOrderExtendMapper.insertOrUpdate(ext);
    }

    @Override
    public int delete(Long id) {
        return tcOrderExtendMapper.deleteById(id);
    }

    @Override
    public int deleteByKeys(Long primaryOrderId, Long orderId, Map<String, ? extends Collection<String>> typeKeys) {
        int total = 0;
        for (Entry<String, ? extends Collection<String>> typeEn : typeKeys.entrySet()) {
            String type = typeEn.getKey();
            Collection<String> keys = typeEn.getValue();

            LambdaUpdateWrapper<TcOrderExtendDO> u = Wrappers.lambdaUpdate();
            u.eq(TcOrderExtendDO::getPrimaryOrderId, primaryOrderId)
                    .eq(TcOrderExtendDO::getOrderId, orderId)
                    .eq(TcOrderExtendDO::getExtendType, type)
                    .in(TcOrderExtendDO::getExtendKey, keys);
            int delete = tcOrderExtendMapper.delete(u);
            total += delete;
        }
        return total;
    }

    @Override
    public int deleteByTypes(Long primaryOrderId, Long orderId, Collection<String> types) {
        LambdaUpdateWrapper<TcOrderExtendDO> u = Wrappers.lambdaUpdate();
        u.eq(TcOrderExtendDO::getPrimaryOrderId, primaryOrderId)
                .eq(TcOrderExtendDO::getOrderId, orderId)
                .in(TcOrderExtendDO::getExtendType, types);
        return tcOrderExtendMapper.delete(u);
    }
}
