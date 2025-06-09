package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.middleware.mq.esindex.OrderEsIndexMqClient;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderCreateEdgeAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderSaveHandler extends AdapterHandler<TOrderCreate> {
    private static final String SAVED_FLAG = "CreateOrderSaveHandler.saved";


    @Autowired
    private OrderCreateService orderCreateService;
    @Autowired
    private OrderCreateEdgeAbility orderCreateEdgeAbility;
    @Autowired(required = false)
    private OrderEsIndexMqClient orderEsIndexMqClient;
    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Value("${search.type:opensearch}")
    private String searchType;

    @Override
    public void handle(TOrderCreate inbound) {
        /*orderCreateService.saveOrder(inbound.getDomain());
        inbound.putExtra(SAVED_FLAG, true);
        orderCreateEdgeAbility.orderSaved(inbound.getDomain());
        pushMqDataEs(inbound.getDomain());*/
    }

    @Override
    public void handleError(TOrderCreate inbound) {
        if (!Boolean.TRUE.equals(inbound.getExtra(SAVED_FLAG))) {
            orderCreateEdgeAbility.failedWithoutSave(inbound.getDomain());
        }
    }
}
