package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderMessageHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder ord = inbound.getDomain();
        for (MainOrder main : ord.getMainOrders()) {
            orderChangedNotifyService.afterStatusChange(OrderChangeNotify.builder()
                    .mainOrder(main)
                    .op(OrderChangeOperateEnum.CUST_CREATE_ORDER)
                    .build());
        }
    }
}
