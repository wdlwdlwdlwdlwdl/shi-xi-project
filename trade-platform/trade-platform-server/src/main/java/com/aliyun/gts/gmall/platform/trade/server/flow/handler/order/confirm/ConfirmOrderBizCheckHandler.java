package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCheckAbility;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderBizCheckHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private OrderBizCheckAbility orderBizCheckAbility;

    @Override
    public void handle(TOrderConfirm inbound) {
        TradeBizResult result = orderBizCheckAbility.checkOnConfirmOrder(inbound.getDomain());
        if (!result.isSuccess()) {
            inbound.setError(result.getFail());
        }
    }
}
