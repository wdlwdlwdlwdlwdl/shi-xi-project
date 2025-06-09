package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.search;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSearchService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomerOrderSearchParamHandler implements
    ProcessFlowNodeHandler<CustomerOrderQueryRpcReq, SimpleSearchRequest> {

    @Autowired
    OrderSearchService orderSearchService;

    @Override
    public SimpleSearchRequest handleBiz(Map<String, Object> context, CustomerOrderQueryRpcReq req) {
        TradeBizResult<SimpleSearchRequest> result = orderSearchService.preProcess(req);
        if(result.isSuccess()){
            return result.getData();
        }else{
            throw new GmallException(OrderErrorCode.ORDER_QUERY_PARAM_ERROR, JSON.toJSONString(req),
                result.getFail().getMessage());
        }
    }
}
