package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepOrderRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcStepOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TcStepOrderRepositoryImpl implements TcStepOrderRepository {

    @Autowired
    private TcStepOrderMapper tcStepOrderMapper;

    @Override
    public void insert(TcStepOrderDO stepOrder) {
        Date now = new Date();
        stepOrder.setGmtCreate(now);
        stepOrder.setGmtModified(now);
        stepOrder.setVersion(1L);
        tcStepOrderMapper.insert(stepOrder);
    }

    @Override
    public List<TcStepOrderDO> queryByPrimaryId(Long primaryOrderId) {
        LambdaQueryWrapper<TcStepOrderDO> q = Wrappers.lambdaQuery();
        return tcStepOrderMapper.selectList(q
                .eq(TcStepOrderDO::getPrimaryOrderId, primaryOrderId)
                .orderByAsc(TcStepOrderDO::getStepNo));
    }

    @Override
    public boolean updateByUkVersion(TcStepOrderDO up) {
        LambdaUpdateWrapper<TcStepOrderDO> u = Wrappers.lambdaUpdate();
        long oldVersion = up.getVersion();
        up.setVersion(oldVersion + 1);
        up.setGmtModified(new Date());
        int update = tcStepOrderMapper.update(up, u
                .eq(TcStepOrderDO::getPrimaryOrderId, up.getPrimaryOrderId())
                .eq(TcStepOrderDO::getStepNo, up.getStepNo())
                .eq(TcStepOrderDO::getVersion, oldVersion));
        return update > 0;
    }
}
