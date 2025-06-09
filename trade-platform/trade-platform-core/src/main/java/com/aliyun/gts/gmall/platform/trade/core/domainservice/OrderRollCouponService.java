package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

/**
 * 退券服务接口
 * @anthor shifeng
 * 2025-3-21 10:00:31
 */
public interface OrderRollCouponService {

    /**
     * 订单退券
     * @param mainOrder
     */
    void orderRollCoupon(MainOrder mainOrder);

}
