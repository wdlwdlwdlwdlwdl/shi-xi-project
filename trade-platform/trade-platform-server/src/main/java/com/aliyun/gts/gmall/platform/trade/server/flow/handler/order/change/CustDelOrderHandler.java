package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.change;

import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustDelOrderHandler implements ProcessFlowNodeHandler<Long, FlowNodeResult> {

    @Autowired
    OrderStatusService orderStatusService;

    @Override
    public FlowNodeResult handleBiz(Map<String, Object> map, Long primaryId) {
        orderStatusService.customerDeleteOrder(primaryId);
        return FlowNodeResult.ok();
    }
}
