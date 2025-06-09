package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;

/**
 * 积分商城固定 现金+积分 下单
 */
public interface FixedPointPriceService {

    boolean isFixedOrder(CreatingOrder order);

    OrderPromotion getFixedOrderPromotion(CreatingOrder order);

    void fillOrderPoints(CreatingOrder order);

    void fillFreight(CreatingOrder order);
}
