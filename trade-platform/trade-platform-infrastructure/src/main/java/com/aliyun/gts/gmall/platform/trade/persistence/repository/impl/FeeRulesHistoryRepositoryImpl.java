package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcFeeRulesHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.DeliveryFeeHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.FeeRulesHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcDeliveryFeeHistoryMapper;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcFeeRulesHistoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class FeeRulesHistoryRepositoryImpl implements FeeRulesHistoryRepository {

    @Autowired
    private TcFeeRulesHistoryMapper tcFeeRulesHistoryMapper;


    @Override
    public List<TcFeeRulesHistoryDO> queryFeeRulesList(String feeRulesCode) {
        LambdaQueryWrapper<TcFeeRulesHistoryDO> q = Wrappers.lambdaQuery();
        return tcFeeRulesHistoryMapper.selectList(q
                .eq(TcFeeRulesHistoryDO::getFeeRulesCode , feeRulesCode)
                .orderByDesc(TcFeeRulesHistoryDO::getGmtModified));
    }

    @Override
    public TcFeeRulesHistoryDO saveFeeRulesHistory(TcFeeRulesHistoryDO tcFeeRulesHistoryDO) {
        tcFeeRulesHistoryMapper.insert(tcFeeRulesHistoryDO);
        Long id = tcFeeRulesHistoryDO.getId();
        if (id != null) {
            return tcFeeRulesHistoryMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }
}
