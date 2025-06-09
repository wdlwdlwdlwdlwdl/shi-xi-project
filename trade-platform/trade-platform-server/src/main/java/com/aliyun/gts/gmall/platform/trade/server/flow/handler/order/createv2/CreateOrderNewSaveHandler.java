package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.middleware.mq.esindex.OrderEsIndexMqClient;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderCreateEdgeAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单创建 step 13
 *    金额支付
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewSaveHandler extends AdapterHandler<TOrderCreate> {

    public static final String SAVED_FLAG = "CreateOrderSaveHandler.saved";

    public static final Integer INSERT = 1;

    public static final Integer PRIMARY_ORDER_FLAG = 1;

    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private OrderCreateEdgeAbility orderCreateEdgeAbility;

    @Autowired(required = false)
    private OrderEsIndexMqClient orderEsIndexMqClient;

    @Override
    public void handle(TOrderCreate inbound) {
        // 订单保存
        orderCreateService.saveOrder(inbound.getDomain());
        // 订单扩展保存
        inbound.putExtra(SAVED_FLAG, true);
        //保存成后扩展
        orderCreateEdgeAbility.orderSaved(inbound.getDomain());
    }

    @Override
    public void handleError(TOrderCreate inbound) {
        if (!Boolean.TRUE.equals(inbound.getExtra(SAVED_FLAG))) {
            orderCreateEdgeAbility.failedWithoutSave(inbound.getDomain());
        }
    }
}
