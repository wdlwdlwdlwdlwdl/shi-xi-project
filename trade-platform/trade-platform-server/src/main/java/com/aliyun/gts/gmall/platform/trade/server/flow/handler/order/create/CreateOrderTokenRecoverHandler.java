package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderTokenRecoverHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderCreateService orderCreateService;

    @Override
    public void handle(TOrderCreate inbound) {
        // 下单成功, 失效token
        orderCreateService.recoverToken(inbound.getDomain(), false);
    }

    @Override
    public void handleError(TOrderCreate inbound) {
        // 下单失败, token 继续可用
        orderCreateService.recoverToken(inbound.getDomain(), true);
    }
}
