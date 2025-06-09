package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import java.util.List;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;

public interface OrderStatusChangeExt extends IExtensionPoints {

    @AbilityExtension(
        code = "ORDER_STATUS_CHANGE",
        name = "订单状态变更",
        description = "订单状态变更"
    )
    TradeBizResult<List<TcOrderDO>> orderStatusChange(OrderStatus orderStatus);

    @AbilityExtension(
            code = "getOrderStatusOnPaySuccess",
            name = "订单变更计算",
            description = "订单变更计算"
    )
    Integer getOrderStatusOnPaySuccess(MainOrder mainOrder, OrderPay orderPay);
}
