package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.domain.entity.deposit.ItemDepositInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

public interface DepositDomainService {

    /**
     * 是否定金尾款商品 (下单时用)
     */
    boolean isDepositItem(ItemSku itemSku);

    /**
     * 获取定金信息（下单时）
     */
    ItemDepositInfo getDepositFromItems(MainOrder mainOrder);

    /**
     * 是否定金尾款订单 (非下单时用)
     */
    boolean isDepositOrder(MainOrder mainOrder);
}
