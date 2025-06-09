package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;

import java.util.Map;

/**
 * 寻源决标单的 下单交易扩展
 */
public interface B2bSourcingDomainService {


    // ========== 确认订单 & 下单 - 扩展决标单商品价格 ==============

    boolean isSourcing(CreatingOrder order);

    OrderPromotion getSourcingPromotion(CreatingOrder order);

    void fillFreight(CreatingOrder order);

    void getOrderFeature(CreatingOrder order, MainOrder main, Map<String, String> map);

    void getOrderFeature(CreatingOrder order, MainOrder main, SubOrder sub, Map<String, String> map);


    // ========== 下单 - 决标单锁定、更新 ==============

    void beginCreateOrder(CreatingOrder order);

    void endCreateOrder(CreatingOrder order, boolean success);


    // ========== 订单成功后 - 检查、重试决标单更新 ==============

    boolean isSourcing(MainOrder mainOrder);

    void consumeOrderCreated(MainOrder mainOrder);
}
