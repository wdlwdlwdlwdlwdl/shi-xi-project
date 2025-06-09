package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

public interface EvoucherJudgementService {

    /**
     * 是否电子凭证商品 (下单时用)
     */
    boolean isEvItem(ItemSku itemSku);

    /**
     * 是否电子凭证订单 (非下单时用)
     */
    boolean isEvOrder(MainOrder mainOrder);
}
