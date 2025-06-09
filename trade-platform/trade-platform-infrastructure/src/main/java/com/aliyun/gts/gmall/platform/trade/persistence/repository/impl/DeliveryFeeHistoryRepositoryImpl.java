package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.DeliveryFeeHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCancelReasonHistoryMapper;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcDeliveryFeeHistoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DeliveryFeeHistoryRepositoryImpl implements DeliveryFeeHistoryRepository {

    @Autowired
    private TcDeliveryFeeHistoryMapper tcDeliveryFeeHistoryMapper;


    @Override
    public List<TcDeliveryFeeHistoryDO> queryDeliveryFeeList(String feeCode) {
        LambdaQueryWrapper<TcDeliveryFeeHistoryDO> q = Wrappers.lambdaQuery();
        return tcDeliveryFeeHistoryMapper.selectList(q
            .eq(TcDeliveryFeeHistoryDO::getFeeCode , feeCode)
            .orderByDesc(TcDeliveryFeeHistoryDO::getGmtModified));
    }

    @Override
    public TcDeliveryFeeHistoryDO saveDeliveryFeeHistory(TcDeliveryFeeHistoryDO tcDeliveryFeeHistoryDO) {
        tcDeliveryFeeHistoryMapper.insert(tcDeliveryFeeHistoryDO);
        Long id = tcDeliveryFeeHistoryDO.getId();
        if (id != null) {
            return tcDeliveryFeeHistoryMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }
}
