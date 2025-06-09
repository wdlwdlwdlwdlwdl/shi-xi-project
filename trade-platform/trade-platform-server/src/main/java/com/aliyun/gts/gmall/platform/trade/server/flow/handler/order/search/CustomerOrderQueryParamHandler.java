package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.search;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderQueryService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 已买到订单列表数据库查询start
 * step1
 * 准备DB参数
 */
@Component
public class CustomerOrderQueryParamHandler implements
    ProcessFlowNodeHandler<CustomerOrderQueryRpcReq, OrderQueryWrapper> {

    @Autowired
    private OrderQueryService orderQueryService;

    @Override
    public OrderQueryWrapper handleBiz(Map<String, Object> context, CustomerOrderQueryRpcReq req) {
        // 订单查询
        TradeBizResult<OrderQueryWrapper> result = orderQueryService.preProcess(req);
        if(result.isSuccess()){
            return result.getData();
        }else{
            throw new GmallException(
                OrderErrorCode.ORDER_QUERY_PARAM_ERROR,
                JSON.toJSONString(req),
                result.getFail().getMessage()
            );
        }
    }
}
