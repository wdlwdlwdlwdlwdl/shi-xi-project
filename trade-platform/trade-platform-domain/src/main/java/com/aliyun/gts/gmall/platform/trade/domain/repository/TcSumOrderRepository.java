package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderSummaryDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TcSumOrder;

import java.util.List;

/**
 * Created by auto-generated on 2021/02/04.
 */
public interface TcSumOrderRepository {

    List<TcOrderSummaryDO> querySummaryList(TcSumOrder req);

    void saveSummary(TcOrderSummaryDO tcOrderSummaryDO);
}
