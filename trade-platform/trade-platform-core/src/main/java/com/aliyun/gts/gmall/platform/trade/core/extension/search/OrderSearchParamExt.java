package com.aliyun.gts.gmall.platform.trade.core.extension.search;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderSearchRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.SellerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import org.elasticsearch.action.search.SearchRequest;

public interface OrderSearchParamExt extends IExtensionPoints {

    @AbilityExtension(
        code = "CUSTOMER_SEARCH_PRE_PROCESS",
        name = "已买到搜索条件准备",
        description = "已买到搜索条件准备"
    )
    TradeBizResult<SimpleSearchRequest> preProcess(CustomerOrderQueryRpcReq req);

    @AbilityExtension(
        code = "SELLER_SEARCH_PRE_PROCESS",
        name = "已卖出搜索条件准备",
        description = "已卖出搜索条件准备"
    )
    TradeBizResult<SimpleSearchRequest> preProcess(SellerOrderQueryRpcReq req);

    @AbilityExtension(
            code = "COMMON_SEARCH_PRE_PROCESS",
            name = "通用搜索条件准备",
            description = "通用搜索条件准备"
    )
    TradeBizResult<SimpleSearchRequest> preProcess(OrderSearchRpcReq req);

    @AbilityExtension(
            code = "CUSTOMER_ES_SEARCH_PRE_PROCESS",
            name = "已买到搜索条件准备",
            description = "已买到搜索条件准备"
    )
    TradeBizResult<SearchRequest> preEsProcess(CustomerOrderQueryRpcReq req);

    @AbilityExtension(
            code = "SELLER_ES_SEARCH_PRE_PROCESS",
            name = "已卖出搜索条件准备",
            description = "已卖出搜索条件准备"
    )
    TradeBizResult<SearchRequest> preEsProcess(SellerOrderQueryRpcReq req);

    @AbilityExtension(
            code = "COMMON_ES_SEARCH_PRE_PROCESS",
            name = "通用搜索条件准备",
            description = "通用搜索条件准备"
    )
    TradeBizResult<SearchRequest> preEsProcess(OrderSearchRpcReq req);
}
