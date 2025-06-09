package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单确认 step10
 *    生成用于下单的的token
 */
@Component
public class ConfirmOrderNewTokenHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private OrderCreateService orderCreateService;

    @Override
    public void handle(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq = inbound.getReq();
        CreatingOrder creatingOrder = inbound.getDomain();
        String token = orderCreateService.getOrderToken(
            creatingOrder,
            confirmOrderInfoRpcReq.getConfirmOrderToken()
        );
        inbound.putExtra("token", token);
    }
}
