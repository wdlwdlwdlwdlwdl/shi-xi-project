package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderInventoryHandler extends AdapterHandler<TOrderConfirm> {

    @Override
    public void handle(TOrderConfirm inbound) {
        /*ConfirmOrderInfoRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();

        // 校验库存数据
        for (MainOrder main : order.getMainOrders()) {
            for (SubOrder sub : main.getSubOrders()) {
                if (sub.getItemSku().getSkuQty() < sub.getOrderQty()) {
                    inbound.setError(OrderErrorCode.INVENTORY_NOT_ENOUGH);
                    return;
                }
            }
        }*/
    }
}
