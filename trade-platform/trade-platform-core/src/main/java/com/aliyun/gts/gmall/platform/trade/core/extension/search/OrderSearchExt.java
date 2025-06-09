package com.aliyun.gts.gmall.platform.trade.core.extension.search;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import com.aliyun.gts.gmall.searcher.common.domain.request.SkuSalesSearchRequest;
import org.elasticsearch.action.search.SearchRequest;

public interface OrderSearchExt  extends IExtensionPoints {

    @AbilityExtension(
        code = "ORDER_SEARCH",
        name = "订单列表搜索",
        description = "订单列表搜索"
    )
    TradeBizResult<ListOrder> search(SimpleSearchRequest request);

    @AbilityExtension(
            code = "ORDER_ES_SEARCH",
            name = "订单es搜索",
            description = "订单列表搜搜"
    )
    TradeBizResult<ListOrder> search(SearchRequest request);


    @AbilityExtension(
            code = "ORDER_ES_SEARCH_WITH_SKU",
            name = "订单es搜索通过SKU",
            description = "订单列表搜搜SKU"
    )
    TradeBizResult<Double> searchForSkuSales(SkuSalesSearchRequest request);



    TradeBizResult<Long> searchLastSaleDate(SkuSalesSearchRequest request);
}
