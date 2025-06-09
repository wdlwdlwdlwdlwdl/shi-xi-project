package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.search;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.SellerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSearchService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SellerOrderEsSearchParamHandler implements
    ProcessFlowNodeHandler<SellerOrderQueryRpcReq, SearchRequest> {

    @Autowired
    OrderSearchService orderSearchService;

    @Override
    public SearchRequest handleBiz(Map<String, Object> context, SellerOrderQueryRpcReq req) {
        TradeBizResult<SearchRequest> result = orderSearchService.preEsProcess(req);
        if(result.isSuccess()){
            return result.getData();
        } else {
            throw new GmallException(
                OrderErrorCode.ORDER_QUERY_PARAM_ERROR,
                JSON.toJSONString(req) ,
                result.getFail().getMessage()
            );
        }
    }
}
