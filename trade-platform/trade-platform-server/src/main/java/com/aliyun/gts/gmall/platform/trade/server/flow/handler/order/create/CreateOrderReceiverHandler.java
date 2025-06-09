package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderReceiverAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderReceiverHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderReceiverAbility orderReceiverAbility;
    @Autowired
    private TcOrderConverter tcOrderConverter;

    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();

        /*TradeBizResult<ReceiveAddr> result = orderReceiverAbility.checkOnCreateOrder(req.getCustId(), order.getReceiver(), order);
        if (result.isSuccess()) {
            order.setReceiver(result.getData());
            orderReceiverAbility.fillLogisticsInfo(order);
        } else {
            inbound.setError(result.getFail());
        }*/
    }
}
