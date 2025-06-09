package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;

/**
 * 积分汇率
 */
public interface PointExchangeRepository {

    /**
     * 兑换比例
     */
    PointExchange getExchangeRate();
}
