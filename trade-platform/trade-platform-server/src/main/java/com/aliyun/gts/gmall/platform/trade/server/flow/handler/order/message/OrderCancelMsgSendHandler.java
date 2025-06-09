package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.message;

import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCancelNotifyService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperate;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OrderCancelMsgSendHandler implements
        ProcessFlowNodeHandler<List<TcOrderDO>, FlowNodeResult> {

    @Autowired
    private OrderCancelNotifyService orderCancelNotifyService;

    @Override
    public FlowNodeResult handleBiz(Map<String, Object> map, List<TcOrderDO> tcOrderDOS) {
        OrderChangeOperate op = (OrderChangeOperate) map.get("op");
        if (op == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        OrderStatus status = (OrderStatus) map.get("orderStatus");
        orderCancelNotifyService.afterStatusChange(
            OrderChangeNotify.builder()
            .orderList(tcOrderDOS)
            .op(op)
            .fromOrderStatus(status.getCheckStatus().getCode())
                    .cancelFromStatus(status.getCancelFromStatus().getCode())
            .build()
        );
        return FlowNodeResult.ok();
    }
}
