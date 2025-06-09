package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.StepOrderCreateAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.GenerateIdService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderPaymentHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private GenerateIdService generateIdService;
    @Autowired
    private StepOrderCreateAbility stepOrderCreateAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();
    }
}
