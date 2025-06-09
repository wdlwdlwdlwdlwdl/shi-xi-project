package com.aliyun.gts.gmall.center.trade.server.flow.handler.cartAdd;

import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ItemRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartAdd;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddCartItemHandlerCenter extends AdapterHandler<TCartAdd> {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void handle(TCartAdd inbound) {
        CartItem cart = inbound.getDomain();
        ItemSku item = itemRepository.queryItemRequired(new ItemSkuId(cart.getItemId(), cart.getSkuId()));
        cart.setItemSku(item);
        cart.setSellerId(cart.getSellerId());
    }
}
