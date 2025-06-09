package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.component.OrderStatusChangeFlowComponent;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSysProcessService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SystemRefund;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderSysProcessServiceImpl implements OrderSysProcessService {

    @Autowired
    private ReversalCreateService reversalCreateService;
    @Autowired
    private OrderStatusChangeFlowComponent orderStatusChangeFlowComponent;
    @Autowired
    private WorkflowProperties workflowProperties;

    @Override
    public MainReversal systemRefund(SystemRefund refund) {
        return reversalCreateService.createSystemRefund(refund);
    }

    @Override
    public void systemConfirm(MainOrder mainOrder) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setStatus(OrderStatusEnum.CANCELLED);
        orderStatus.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        orderStatus.setCheckStatus(OrderStatusEnum.codeOf(mainOrder.getPrimaryOrderStatus()));

        FlowNodeResult result = orderStatusChangeFlowComponent.changeOrderStatus(
                workflowProperties.getOrderConfirmReceive(),
                BizCodeEntity.buildWithDefaultBizCode(mainOrder),
                orderStatus,
                OrderChangeOperateEnum.CANCELLED);
        if (!result.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(result.getFail()));
        }
    }
}
