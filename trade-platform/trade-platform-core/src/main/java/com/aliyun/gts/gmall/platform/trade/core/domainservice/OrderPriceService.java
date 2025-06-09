package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.AdjustPrice;

import java.util.List;

public interface OrderPriceService {

    /**
     * 计算订单优惠与价格, 确认订单使用
     */
    void calcOrderPrice(CreatingOrder order);

    /**
     * 计算订单优惠与价格, 确认订单使用
     */
    void calcOrderPriceNew(CreatingOrder order);

    /**
     * 重新计算订单优惠与价格，并校验与传入值是否一致, 下单使用
     */
    void recalcOrderPrice(CreatingOrder order);

    /**
     * 改价, 并保存, 不发消息
     */
    List<OrderChangeNotify> adjustPrice(MainOrder mainOrder, AdjustPrice adj);
}
