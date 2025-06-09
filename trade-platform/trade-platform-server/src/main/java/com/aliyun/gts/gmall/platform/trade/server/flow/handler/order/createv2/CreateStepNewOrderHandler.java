package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.StepOrderDomainService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单创建 step 9
 *    多步订单计算
 *    预售订单计算 预售只算首付款
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateStepNewOrderHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private StepOrderDomainService stepOrderDomainService;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder creatingOrder = inbound.getDomain();
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            // 是否是多步骤订单  预售活动
            if (StepOrderUtils.isMultiStep(mainOrder)) {
                stepOrderDomainService.checkStepInfoOnCreate(mainOrder);
            }
        }
    }
}
