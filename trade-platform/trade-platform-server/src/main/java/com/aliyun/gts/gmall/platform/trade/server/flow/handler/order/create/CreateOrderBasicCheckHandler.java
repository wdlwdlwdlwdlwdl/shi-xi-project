package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderBasicCheckHandler extends AdapterHandler<TOrderCreate> {

    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();

    }
}
