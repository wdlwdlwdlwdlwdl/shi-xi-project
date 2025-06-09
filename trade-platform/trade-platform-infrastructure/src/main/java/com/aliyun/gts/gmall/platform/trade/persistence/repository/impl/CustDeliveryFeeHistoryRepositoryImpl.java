package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCustDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CustDeliveryFeeHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCustDeliveryFeeHistoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CustDeliveryFeeHistoryRepositoryImpl implements CustDeliveryFeeHistoryRepository {

    @Autowired
    private TcCustDeliveryFeeHistoryMapper tcCustDeliveryFeeHistoryMapper;


    @Override
    public List<TcCustDeliveryFeeHistoryDO> queryCustDeliveryFeeList(String feeCode) {
        LambdaQueryWrapper<TcCustDeliveryFeeHistoryDO> q = Wrappers.lambdaQuery();
        return tcCustDeliveryFeeHistoryMapper.selectList(q
                .eq(TcCustDeliveryFeeHistoryDO::getCustFeeCode , feeCode)
                .orderByDesc(TcCustDeliveryFeeHistoryDO::getGmtModified));
    }

    @Override
    public TcCustDeliveryFeeHistoryDO saveCustDeliveryFeeHistory(TcCustDeliveryFeeHistoryDO tcCustDeliveryFeeHistoryDO) {
        tcCustDeliveryFeeHistoryMapper.insert(tcCustDeliveryFeeHistoryDO);
        Long id = tcCustDeliveryFeeHistoryDO.getId();
        if (id != null) {
            return tcCustDeliveryFeeHistoryMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }
}
