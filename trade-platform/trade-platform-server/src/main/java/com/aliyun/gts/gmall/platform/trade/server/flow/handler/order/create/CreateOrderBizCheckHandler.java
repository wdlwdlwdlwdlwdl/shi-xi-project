package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCheckAbility;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderBizCheckHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderBizCheckAbility orderBizCheckAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        TradeBizResult result = orderBizCheckAbility.checkOnCreateOrder(inbound.getDomain());
        if (!result.isSuccess()) {
            inbound.setError(result.getFail());
        }
    }
}
