package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;

import java.util.List;

public interface OrderCancelService {

    TradeBizResult<List<TcOrderDO>> autoCancel(OrderStatus orderStatus);

}
