package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.domain.entity.deposit.ItemDepositInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;

public interface PresaleDomainService {

    /**
     * 是否预售商品 (下单时用)
     */
    boolean isPresaleItem(ItemPromotion prom);

    /**
     * 取预售定金/尾款信息
     */
    ItemDepositInfo getDepositFromItems(MainOrder mainOrder);

    /**
     * 是否预售订单 (非下单时用)
     */
    boolean isPresaleOrder(MainOrder mainOrder);
}
