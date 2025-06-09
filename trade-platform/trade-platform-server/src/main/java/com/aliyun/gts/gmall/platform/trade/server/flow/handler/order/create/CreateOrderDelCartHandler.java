package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUkBatch;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CreateOrderDelCartHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();

        // 非购物车下单
        if (order.getIsFromCart() == null || !order.getIsFromCart().booleanValue()) {
            return;
        }

        // 购物车下单
        List<ItemSkuId> items = order.getMainOrders().stream()
                .flatMap(main -> main.getSubOrders().stream())
                .map(sub -> sub.getItemSku().getItemSkuId())
                .collect(Collectors.toList());
        CartItemUkBatch batch = new CartItemUkBatch();
        batch.setCustId(order.getCustomer().getCustId());
        batch.setCartType(order.getCartType());
        batch.setItemSkuIds(items);
        cartService.deleteItems(batch);
    }
}
