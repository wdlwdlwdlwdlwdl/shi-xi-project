package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.search;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSearchService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderSearchHandler implements ProcessFlowNodeHandler<SimpleSearchRequest, ListOrder> {

    @Autowired
    OrderSearchService orderSearchService;

    @Override
    public ListOrder handleBiz(Map<String, Object> map, SimpleSearchRequest request) {
        TradeBizResult<ListOrder> result = orderSearchService.search(request);
        if(result.isSuccess()){
            return result.getData();
        }else{
            throw new GmallException(OrderErrorCode.ORDER_QUERY_PROCESS_ERROR,
                JSON.toJSONString(request) , result.getFail().getMessage());
        }
    }
}
