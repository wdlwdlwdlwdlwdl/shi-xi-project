package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;

import java.util.List;

/**
 * Created by auto-generated on 2021/02/04.
 */
public interface TcLogisticsRepository{

    TcLogisticsDO queryTcLogistics(long id);

    TcLogisticsDO create(TcLogisticsDO tcLogisticsDO);

    TcLogisticsDO update(TcLogisticsDO tcLogisticsDO);

    int delete(Long id);

    void create(List<TcLogisticsDO> tcLogisticsDOS);

    List<TcLogisticsDO> queryByPrimaryId(Long pOrderId, Long pReversalId);

    TcLogisticsDO queryLogisticsByPrimaryId(Long pOrderId, Long pReversalId);
}
