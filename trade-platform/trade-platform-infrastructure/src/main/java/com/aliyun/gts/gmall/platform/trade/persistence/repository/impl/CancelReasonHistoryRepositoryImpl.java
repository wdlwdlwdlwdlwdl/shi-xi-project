package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCancelReasonHistoryMapper;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcSellerConfigMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CancelReasonHistoryRepositoryImpl implements CancelReasonHistoryRepository {

    @Autowired
    private TcCancelReasonHistoryMapper tcCancelReasonHistoryMapper;


    @Override
    public List<TcCancelReasonHistoryDO> queryCancelReasonList(String cancelReasonCode) {
        LambdaQueryWrapper<TcCancelReasonHistoryDO> q = Wrappers.lambdaQuery();
        return tcCancelReasonHistoryMapper.selectList(q
                .eq(TcCancelReasonHistoryDO::getCancelReasonCode , cancelReasonCode)
                .orderByDesc(TcCancelReasonHistoryDO::getGmtModified));
    }

    @Override
    public TcCancelReasonHistoryDO saveCancelReasonHistory(TcCancelReasonHistoryDO tcCancelReasonHistoryDO) {
        tcCancelReasonHistoryMapper.insert(tcCancelReasonHistoryDO);
        Long id = tcCancelReasonHistoryDO.getId();
        if (id != null) {
            return tcCancelReasonHistoryMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }
}
