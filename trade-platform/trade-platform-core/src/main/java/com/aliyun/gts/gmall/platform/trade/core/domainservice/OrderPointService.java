package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;

public interface OrderPointService {

    /**
     * 锁定积分, 前置条件:积分分摊计算好
     */
    void lockPoint(CreatingOrder order);

    /**
     * 释放积分锁定
     */
    void unlockPoint(CreatingOrder order);
}
