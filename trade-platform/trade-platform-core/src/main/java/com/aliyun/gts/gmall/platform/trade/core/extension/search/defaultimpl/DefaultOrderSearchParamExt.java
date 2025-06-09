package com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderSearchRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.SellerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchParamExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.core.util.SearchUtils;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultOrderSearchParamExt implements OrderSearchParamExt {

    @Override
    public TradeBizResult<SimpleSearchRequest> preProcess(CustomerOrderQueryRpcReq req) {
        SimpleSearchRequest request = SearchUtils.convertQueryToSearchRequest(req);
        return TradeBizResult.ok(request);
    }

    @Override
    public TradeBizResult<SimpleSearchRequest> preProcess(SellerOrderQueryRpcReq req) {
        SimpleSearchRequest request = SearchUtils.convertQueryToSearchRequest(req);
        return TradeBizResult.ok(request);
    }

    @Override
    public TradeBizResult<SimpleSearchRequest> preProcess(OrderSearchRpcReq req) {
        SimpleSearchRequest request = SearchUtils.convertQueryToSearchRequest(req);
        return TradeBizResult.ok(request);
    }

    @Override
    public TradeBizResult<SearchRequest> preEsProcess(CustomerOrderQueryRpcReq req) {
        return TradeBizResult.ok(SearchUtils.convertQueryToEsSearchReq(req));
    }

    @Override
    public TradeBizResult<SearchRequest> preEsProcess(SellerOrderQueryRpcReq req) {
        return TradeBizResult.ok(SearchUtils.convertQueryToEsSearchReq(req));
    }

    @Override
    public TradeBizResult<SearchRequest> preEsProcess(OrderSearchRpcReq req) {
        return TradeBizResult.ok(SearchUtils.convertQueryToEsSearchReq(req));
    }
}
