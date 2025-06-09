package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.common.constants.InventoryReduceType;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderInventoryHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderConfigService orderConfigService;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();
        for (MainOrder main : order.getMainOrders()) {

            /*// 校验库存数量
            for (SubOrder sub : main.getSubOrders()) {
                if (sub.getItemSku().getSkuQty() < sub.getOrderQty()) {
                    inbound.setError(OrderErrorCode.INVENTORY_NOT_ENOUGH);
                    return;
                }
            }*/

            // 记录减库存方式
            SellerTradeConfig cfg = orderConfigService.getSellerConfig(main.getSeller().getSellerId());
            InventoryReduceType type = InventoryReduceType.codeOf(cfg.getInventoryReduceType());
            main.setInventoryReduceType(type.getCode());
        }
    }
}
