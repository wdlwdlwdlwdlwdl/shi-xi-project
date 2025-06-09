package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderSummaryDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TcSumOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcSumOrderRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcSumOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 说明： TODO
 *
 * @author yangl
 * @version 1.0
 * @date 2025/4/3 14:38
 */
@Component
@Slf4j
public class TcSumOrderRepositoryImpl implements TcSumOrderRepository {

    @Autowired
    private TcSumOrderMapper tcSumOrderMapper;

    @Override
    public List<TcOrderSummaryDO> querySummaryList(TcSumOrder req) {
        return tcSumOrderMapper.querySummaryList(req);
    }

    @Override
    public void saveSummary(TcOrderSummaryDO tcOrderSummaryDO) {
        tcSumOrderMapper.insertOrUpdate(tcOrderSummaryDO);
    }
}
