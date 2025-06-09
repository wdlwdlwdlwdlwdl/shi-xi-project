package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.add;

import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ItemRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartAdd;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 添加购物车 业务校验类
 *     商品信息查询
 */
@Component
public class AddCartItemHandler extends AdapterHandler<TCartAdd> {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void handle(TCartAdd inbound) {
        CartItem cart = inbound.getDomain();
        ItemSku item = itemRepository.queryItemRequired(
            new ItemSkuId(cart.getItemId(), cart.getSkuId(), cart.getSellerId())
        );
        if (item == null) {
            inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
        }
    }
}
