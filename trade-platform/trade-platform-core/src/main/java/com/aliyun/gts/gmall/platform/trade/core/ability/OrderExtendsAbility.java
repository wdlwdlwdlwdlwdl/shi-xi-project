package com.aliyun.gts.gmall.platform.trade.core.ability;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;

public interface OrderExtendsAbility {

    /**
     * 新增扩展数据
     *
     * @param order
     */
    void addExtendOnCrete(CreatingOrder order);
}
