package com.aliyun.gts.gmall.platform.trade.core.extension.search;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;

public interface OrderSearchResultExt extends IExtensionPoints {

    @AbilityExtension(
        code = "ORDER_LIST_PROCESS",
        name = "订单列表结果处理",
        description = "订单列表结果处理"
    )
    TradeBizResult<PageInfo<MainOrderDTO>> processResult(ListOrder listOrder);

    TradeBizResult<PageInfo<MainOrderDTO>> processCustResult(ListOrder listOrder);

    TradeBizResult<PageInfo<MainOrderDTO>> processAdminResult(ListOrder listOrder);

}
