package com.aliyun.gts.gmall.center.trade.server.flow.handler.orderConfirm;

import com.aliyun.gts.gmall.middleware.mq.esindex.OrderEsIndexMqClient;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderCreateEdgeAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderSaveHandler extends AdapterHandler<TOrderConfirm> {

    private static final String SAVED_FLAG = "CreateOrderSaveHandler.saved";

    public static final Integer INSERT = 1;
    public static final Integer PRIMARY_ORDER_FLAG = 1;

    @Value("${search.type:opensearch}")
    private String searchType;

    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private OrderCreateEdgeAbility orderCreateEdgeAbility;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired(required = false)
    private OrderEsIndexMqClient orderEsIndexMqClient;

    @Override
    public void handle(TOrderConfirm inbound) {
        orderCreateService.saveOrderForConfirm(inbound.getDomain());
        inbound.putExtra(SAVED_FLAG, true);
        orderCreateEdgeAbility.orderSaved(inbound.getDomain());

    }

    @Override
    public void handleError(TOrderConfirm inbound) {
        if (!Boolean.TRUE.equals(inbound.getExtra(SAVED_FLAG))) {
            orderCreateEdgeAbility.failedWithoutSave(inbound.getDomain());
        }
    }
}
