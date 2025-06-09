package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单创建 step 15
 *     token回收处理,
 *     下单异常的token继续可用,
 *     下单成功的token失效掉
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewTokenRecoverHandler extends AdapterHandler<TOrderCreate> {

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
