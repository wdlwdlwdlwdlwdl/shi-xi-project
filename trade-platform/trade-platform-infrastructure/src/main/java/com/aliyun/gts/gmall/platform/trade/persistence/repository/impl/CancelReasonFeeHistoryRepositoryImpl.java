package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonFeeHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCancelReasonFeeHistoryMapper;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCancelReasonHistoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CancelReasonFeeHistoryRepositoryImpl implements CancelReasonFeeHistoryRepository {

    @Autowired
    private TcCancelReasonFeeHistoryMapper tcCancelReasonFeeHistoryMapper;


    @Override
    public List<TcCancelReasonFeeHistoryDO> queryCancelReasonFeeList(String code) {
        LambdaQueryWrapper<TcCancelReasonFeeHistoryDO> q = Wrappers.lambdaQuery();
        return tcCancelReasonFeeHistoryMapper.selectList(q
                .eq(TcCancelReasonFeeHistoryDO::getReasonFeeCode , code)
                .orderByDesc(TcCancelReasonFeeHistoryDO::getGmtModified));
    }

    @Override
    public TcCancelReasonFeeHistoryDO saveCancelReasonFeeHistory(TcCancelReasonFeeHistoryDO tcCancelReasonFeeHistoryDO) {
        tcCancelReasonFeeHistoryMapper.insert(tcCancelReasonFeeHistoryDO);
        Long id = tcCancelReasonFeeHistoryDO.getId();
        if (id != null) {
            return tcCancelReasonFeeHistoryMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }
}
