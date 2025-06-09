package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;

/**
 * 价格风控
 */
public interface OrderPriceRiskService {

    void check(CreatingOrder order);
}
