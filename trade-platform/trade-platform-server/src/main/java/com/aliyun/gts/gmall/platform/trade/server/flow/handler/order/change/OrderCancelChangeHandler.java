package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.change;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCancelService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrderCancelChangeHandler implements
    ProcessFlowNodeHandler<OrderStatus, List<TcOrderDO>> {

    @Autowired
    private OrderCancelService orderCancelService;

    @Override
    public List<TcOrderDO> handleBiz(Map<String, Object> map, OrderStatus orderStatus) {
        TradeBizResult<List<TcOrderDO>> result = orderCancelService.autoCancel(orderStatus);
        if(result.isSuccess()){
            return result.getData();
        } else {
            throw new GmallException(
                OrderErrorCode.ORDER_STATUS_CHANGE_ERROR ,
                JSON.toJSONString(orderStatus),
                result.getFail().getMessage()
            );
        }
    }
}
