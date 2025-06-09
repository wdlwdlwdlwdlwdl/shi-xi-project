package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;

import java.util.List;

public interface OrderStatusService {

    /**
     * 订单状态修改
     * @param orderStatus
     * @return
     */
    TradeBizResult<List<TcOrderDO>> changeOrderStatus(OrderStatus orderStatus);

    TradeBizResult<List<TcOrderDO>> updateStatus(OrderStatus orderStatus);

    void customerDeleteOrder(Long primaryId);

    TradeBizResult<Boolean> custDelOrderCheck(Long primaryId);

    TradeBizResult<Boolean> custDelOrderCheck(Long primaryId, Long custId);

    void onPaySuccess(MainOrder mainOrder, OrderPay orderPay);

    void payComfirming(MainOrder mainOrder);

    TradeBizResult<List<TcOrderDO>> changeStepOrderStatus(OrderStatus orderStatus);
}
