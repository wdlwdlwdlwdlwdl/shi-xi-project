package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderTokenHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private OrderCreateService orderCreateService;

    @Override
    public void handle(TOrderConfirm inbound) {
        CreatingOrder order = inbound.getDomain();
        String token = orderCreateService.getOrderToken(order, null);
        inbound.putExtra("token", token);
    }
}
