package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.StepOrderDomainService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateStepOrderHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private StepOrderDomainService stepOrderDomainService;

    @Override
    public void handle(TOrderCreate inbound) {
        for (MainOrder mainOrder : inbound.getDomain().getMainOrders()) {
            if (StepOrderUtils.isMultiStep(mainOrder)) {
                stepOrderDomainService.checkStepInfoOnCreate(mainOrder);
            }
        }
    }
}
