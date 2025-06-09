package com.aliyun.gts.gmall.platform.trade.core.extension.search;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.SellerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;

public interface OrderQueryParamExt extends IExtensionPoints {

    @AbilityExtension(
        code = "CUSTOMER_QUERY_PRE_PROCESS",
        name = "已买到DB查询条件准备",
        description = "已买到DB查询条件准备"
    )
    TradeBizResult<OrderQueryWrapper> preProcess(CustomerOrderQueryRpcReq req);


}
