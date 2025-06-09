package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.search;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderQueryService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 发起查询
 * step2
 * 查询数据库里面的订单信息 个人订单列表
 */
@Component
public class OrderQueryHandler implements ProcessFlowNodeHandler<OrderQueryWrapper, ListOrder> {

    @Autowired
    private OrderQueryService orderQueryService;

    @Override
    public ListOrder handleBiz(Map<String, Object> map, OrderQueryWrapper request) {
        // 查询订单（用户个人的）
        TradeBizResult<ListOrder> result = orderQueryService.query(request);
        if(result.isSuccess()){
            return result.getData();
        }else{
            throw new GmallException(
                OrderErrorCode.ORDER_QUERY_PROCESS_ERROR,
                JSON.toJSONString(request) ,
                result.getFail().getMessage()
            );
        }
    }
}
