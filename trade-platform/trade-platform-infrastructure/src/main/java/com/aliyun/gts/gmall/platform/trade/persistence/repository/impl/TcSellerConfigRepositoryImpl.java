package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcSellerConfigDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcSellerConfigRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcSellerConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TcSellerConfigRepositoryImpl implements TcSellerConfigRepository {

    @Autowired
    private TcSellerConfigMapper tcSellerConfigMapper;

    @Override
    public List<TcSellerConfigDO> queryBySeller(Long sellerId) {
        LambdaQueryWrapper<TcSellerConfigDO> q = Wrappers.lambdaQuery();
        return tcSellerConfigMapper.selectList(q
                .in(TcSellerConfigDO::getSellerId, sellerId, 0L));
    }

    @Override
    public void save(List<TcSellerConfigDO> list) {
        for (TcSellerConfigDO conf : list) {
            tcSellerConfigMapper.insertOrUpdate(conf);
        }
    }
}
