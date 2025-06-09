package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderPointService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderPointLockHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderPointService orderPointService;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();

        // 冻结积分
        if (NumUtils.getNullZero(order.getOrderPrice().getPointCount()) > 0) {
            inbound.putExtra("pointLock", true);
            orderPointService.lockPoint(order);
        }
    }
}
