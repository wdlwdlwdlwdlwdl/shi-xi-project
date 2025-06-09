package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.OrderStatisticsDTO;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import org.elasticsearch.action.search.SearchRequest;

import java.util.List;
import java.util.Map;

public interface OrderSearchService {
    TradeBizResult<SimpleSearchRequest> preProcess(CustomerOrderQueryRpcReq req);

    TradeBizResult<SimpleSearchRequest> preProcess(SellerOrderQueryRpcReq req);

    TradeBizResult<SimpleSearchRequest> preProcess(OrderSearchRpcReq req);

    TradeBizResult<SearchRequest> preEsProcess(CustomerOrderQueryRpcReq req);

    TradeBizResult<SearchRequest> preEsProcess(SellerOrderQueryRpcReq req);

    TradeBizResult<SearchRequest> preEsProcess(OrderSearchRpcReq req);

    TradeBizResult<ListOrder> search(SimpleSearchRequest request);

    TradeBizResult<ListOrder> search(SearchRequest request);

    TradeBizResult<PageInfo<MainOrderDTO>> processCustomerResult(ListOrder listOrder);

    TradeBizResult<PageInfo<MainOrderDTO>> processSellerResult(ListOrder listOrder);

    TradeBizResult<PageInfo<MainOrderDTO>> processAdminResult(ListOrder listOrder);

    Map<Integer,Integer> countByStatus(CountOrderQueryRpcReq req);

    List<OrderStatisticsDTO> statisticsBySellerIds(OrderStatisticsQueryRpcReq req);

    List<OrderStatisticsDTO> statisticsBySeller(OrderStatisticsQueryRpcReq req);
}
