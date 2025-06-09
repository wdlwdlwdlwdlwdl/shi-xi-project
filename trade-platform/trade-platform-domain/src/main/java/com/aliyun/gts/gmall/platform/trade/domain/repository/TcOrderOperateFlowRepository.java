package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderOperateFlowQuery;

import java.util.List;

/**
 * Created by auto-generated on 2021/03/23.
 */
public interface TcOrderOperateFlowRepository{


    TcOrderOperateFlowDO queryTcOrderOperateFlow(long id);

    void create(TcOrderOperateFlowDO tcOrderOperateFlowDO);

    int batchCreate(List<TcOrderOperateFlowDO> tcOrderOperateFlowDOS);

    int delete(Long id);

    List<TcOrderOperateFlowDO> queryByPrimaryId(Long primaryOrderId);

    boolean exist(OrderOperateFlowQuery query);
}
