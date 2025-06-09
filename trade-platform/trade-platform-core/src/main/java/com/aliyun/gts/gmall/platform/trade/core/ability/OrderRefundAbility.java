package com.aliyun.gts.gmall.platform.trade.core.ability;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;

/**
 * 订单退款处理扩展类
 *
 * @author yangl
 */
public interface OrderRefundAbility {

    boolean isReturnFee(MainOrder mainOrder);

    void doRefund(OrderStatus orderStatus, MainOrder mainOrder);
}

