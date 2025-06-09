package com.aliyun.gts.gmall.platform.trade.core.extension.search;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;

public interface OrderQueryResultExt extends IExtensionPoints {

    @AbilityExtension(
        code = "ORDER_QUERY_LIST_PROCESS",
        name = "订单列表结果处理",
        description = "订单列表结果处理"
    )
    TradeBizResult<PageInfo<MainOrderDTO>> processResult(ListOrder listOrder);

}
