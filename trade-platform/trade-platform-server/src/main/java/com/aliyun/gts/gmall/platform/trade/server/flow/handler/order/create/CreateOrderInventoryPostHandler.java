package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateOrderInventoryPostHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderInventoryAbility orderInventoryAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();

        // 下单减库存
        /*List<MainOrder> reduceOrders = new ArrayList<>();
        for (MainOrder main : order.getMainOrders()) {
            InventoryReduceType type = InventoryReduceType.codeOf(main.getInventoryReduceType());
            if (type == InventoryReduceType.REDUCE_ON_ORDER) {
                reduceOrders.add(main);
            }
        }
        if (!reduceOrders.isEmpty()) {
            try {
                orderInventoryAbility.reduceInventory(reduceOrders);
            } catch (Exception e) {
                // 忽略, 正常下单
                log.warn("锁定库存成功扣减库存失败", e);
            }
        }*/
    }
}
