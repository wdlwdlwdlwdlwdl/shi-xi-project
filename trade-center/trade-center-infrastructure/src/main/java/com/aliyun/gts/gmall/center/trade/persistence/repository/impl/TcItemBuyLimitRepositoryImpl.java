package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcItemBuyLimitDO;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcItemBuyLimitRepository;
import com.aliyun.gts.gmall.center.trade.persistence.mapper.TcItemBuyLimitMapper;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class TcItemBuyLimitRepositoryImpl implements TcItemBuyLimitRepository {

    @Autowired
    private TcItemBuyLimitMapper tcItemBuyLimitMapper;

    @Override
    public void create(TcItemBuyLimitDO buy) {
        Date now = new Date();
        buy.setGmtCreate(now);
        buy.setGmtModified(now);
        buy.setVersion(1L);
        buy.setUselessKey(buy.getCustId());
        tcItemBuyLimitMapper.insert(buy);
    }

    @Override
    public TcItemBuyLimitDO queryByUk(TcItemBuyLimitDO uk) {
        checkUkRequire(uk);

        LambdaQueryWrapper<TcItemBuyLimitDO> q = Wrappers.lambdaQuery(TcItemBuyLimitDO.class);
        return tcItemBuyLimitMapper.selectOne(q
                .eq(TcItemBuyLimitDO::getCustId, uk.getCustId())
                .eq(TcItemBuyLimitDO::getCampId, uk.getCampId())
                .eq(TcItemBuyLimitDO::getItemId, uk.getItemId()));
    }

    @Override
    public int updateByUkVersion(TcItemBuyLimitDO buy) {
        checkUkRequire(buy);
        if (buy.getVersion() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }

        buy.setGmtModified(new Date());
        Long oldVersion = buy.getVersion();
        buy.setVersion(oldVersion + 1L);
        LambdaUpdateWrapper<TcItemBuyLimitDO> u = Wrappers.lambdaUpdate(TcItemBuyLimitDO.class);
        return tcItemBuyLimitMapper.update(buy, u
                .eq(TcItemBuyLimitDO::getCustId, buy.getCustId())
                .eq(TcItemBuyLimitDO::getCampId, buy.getCampId())
                .eq(TcItemBuyLimitDO::getItemId, buy.getItemId())
                .eq(TcItemBuyLimitDO::getVersion, oldVersion));
    }

    private void checkUkRequire(TcItemBuyLimitDO buy) {
        if (buy.getCustId() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        if (buy.getCampId() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        if (buy.getItemId() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
    }
}
