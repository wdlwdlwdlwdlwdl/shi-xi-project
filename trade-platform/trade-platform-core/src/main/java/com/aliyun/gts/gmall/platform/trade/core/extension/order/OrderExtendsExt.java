package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

public interface OrderExtendsExt extends IExtensionPoints {
    @AbilityExtension(
        code = "ADD_ORDER_EXTENDS",
        name = "订单新增扩展数据、底层以扩展表的形式存储",
        description = "订单新增扩展数据、底层以扩展表的形式存储"
    )
    TradeBizResult addExtendOnCrete(MainOrder mainOrder, CreatingOrder creatingOrder);

}
