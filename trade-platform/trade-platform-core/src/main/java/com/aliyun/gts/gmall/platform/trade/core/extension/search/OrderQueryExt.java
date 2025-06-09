package com.aliyun.gts.gmall.platform.trade.core.extension.search;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;

public interface OrderQueryExt extends IExtensionPoints {

    @AbilityExtension(
        code = "CUSTOMER_QUERY_PROCESS",
        name = "已买到DB查询",
        description = "已买到DB查询"
    )
    TradeBizResult<ListOrder> query(OrderQueryWrapper queryWrapper);

}
