package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.TcEvoucherDO;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvoucherRepository;
import com.aliyun.gts.gmall.center.trade.persistence.mapper.TcEvoucherMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TcEvoucherRepositoryImpl implements TcEvoucherRepository {

    @Autowired
    private TcEvoucherMapper tcEvoucherMapper;

    @Override
    public void create(TcEvoucherDO ev) {
        Date now = new Date();
        ev.setGmtCreate(now);
        ev.setGmtModified(now);
        ev.setVersion(1);
        tcEvoucherMapper.insert(ev);
    }

    @Override
    public List<TcEvoucherDO> queryByOrderId(Long orderId) {
        LambdaQueryWrapper<TcEvoucherDO> q = Wrappers.lambdaQuery();
        return tcEvoucherMapper.selectList(q
                .eq(TcEvoucherDO::getOrderId, orderId));
    }

    @Override
    public TcEvoucherDO queryByEvCode(Long evCode) {
        LambdaQueryWrapper<TcEvoucherDO> q = Wrappers.lambdaQuery();
        return tcEvoucherMapper.selectOne(q
                .eq(TcEvoucherDO::getEvCode, evCode));
    }

    @Override
    public boolean updateByCodeVersion(TcEvoucherDO ev) {
        int version = ev.getVersion();
        ev.setGmtModified(new Date());
        ev.setVersion(version + 1);

        LambdaUpdateWrapper<TcEvoucherDO> up = Wrappers.lambdaUpdate();
        int c = tcEvoucherMapper.update(ev, up
                .eq(TcEvoucherDO::getEvCode, ev.getEvCode())
                .eq(TcEvoucherDO::getVersion, version));
        return c > 0;
    }
}
