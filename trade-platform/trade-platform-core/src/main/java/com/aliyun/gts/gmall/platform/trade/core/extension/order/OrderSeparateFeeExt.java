package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

/**
 * 分账计算扩展
 */
public interface OrderSeparateFeeExt extends IExtensionPoints {

    /**
     * 下单时记录分账规则
     */
    void storeSeparateRule(MainOrder mainOrder);

    /**
     * 记录确认收货价格, 含分账信息
     */
    void storeConfirmPrice(MainOrder mainOrder);
}
