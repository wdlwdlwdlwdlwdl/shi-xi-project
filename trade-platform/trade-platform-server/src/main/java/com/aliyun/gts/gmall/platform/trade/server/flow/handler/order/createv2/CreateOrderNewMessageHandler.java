package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单创建 step 17
 *     下单后状态变更消息推送
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewMessageHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder creatingOrder = inbound.getDomain();
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            orderChangedNotifyService
                .afterStatusChange(
                    OrderChangeNotify
                    .builder()
                    .mainOrder(mainOrder)
                    .op(OrderChangeOperateEnum.CUST_CREATE_ORDER)
                    .build()
                );
        }
    }
}
