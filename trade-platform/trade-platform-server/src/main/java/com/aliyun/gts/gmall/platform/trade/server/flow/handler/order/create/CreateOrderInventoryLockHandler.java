package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderInventoryLockHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderInventoryAbility orderInventoryAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        inbound.putExtra("inventoryLock", true);
        /*boolean success = orderInventoryAbility.lockInventory(inbound.getDomain().getMainOrders());
        if (!success) {
            inbound.putExtra("inventoryLock", null);
            inbound.setError(OrderErrorCode.INVENTORY_NOT_ENOUGH);
        }*/
    }
}
