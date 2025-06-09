package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderOperateFlowQuery;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcOrderOperateFlowMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class TcOrderOperateFlowRepositoryImpl implements TcOrderOperateFlowRepository {

    @Autowired
    private TcOrderOperateFlowMapper tcOrderOperateFlowMapper;

    @Override
    public TcOrderOperateFlowDO queryTcOrderOperateFlow(long id) {
        return tcOrderOperateFlowMapper.selectById(id);
    }

    @Override
    public void create(TcOrderOperateFlowDO flow) {
        Date now = new Date();
        flow.setGmtCreate(now);
        flow.setGmtModified(now);

        tcOrderOperateFlowMapper.insert(flow);
    }

    @Override
    public int batchCreate(List<TcOrderOperateFlowDO> tcOrderOperateFlowDOS) {
        if (CollectionUtils.isEmpty(tcOrderOperateFlowDOS)) {
            return 0;
        }
        Date now = new Date();
        for (TcOrderOperateFlowDO flow : tcOrderOperateFlowDOS) {
            if(flow.getOperatorType()==null){
                flow.setOperatorType(0);
            }
            flow.setGmtCreate(now);
            flow.setGmtModified(now);
        }
        try {
            return tcOrderOperateFlowMapper.batchCreate(tcOrderOperateFlowDOS);
        } catch (Exception e) {
            log.error("batchInsertTcOrderLifecycleLog occurred exception!", e);
            return 0;
        }
    }

    @Override
    public int delete(Long id) {
        return tcOrderOperateFlowMapper.deleteById(id);
    }

    @Override
    public List<TcOrderOperateFlowDO> queryByPrimaryId(Long primaryOrderId) {
        LambdaQueryWrapper<TcOrderOperateFlowDO> query = Wrappers.lambdaQuery();
        query.eq(TcOrderOperateFlowDO::getPrimaryOrderId, primaryOrderId)
            .orderByAsc(TcOrderOperateFlowDO::getGmtCreate);
        return tcOrderOperateFlowMapper.selectList(query);
    }

    @Override
    public boolean exist(OrderOperateFlowQuery query){
        LambdaQueryWrapper<TcOrderOperateFlowDO> wrapper = Wrappers.lambdaQuery();
        TcOrderOperateFlowDO  orderOperateFlowDO = tcOrderOperateFlowMapper.selectOne(wrapper
                .eq(TcOrderOperateFlowDO::getPrimaryOrderId, query.getPrimaryOrderId())
                .eq(TcOrderOperateFlowDO::getToOrderStatus, query.getToOrderStatus())
                .eq(TcOrderOperateFlowDO::getFromOrderStatus, query.getFromOrderStatus()));
        return Objects.nonNull(orderOperateFlowDO);
    }
}
