package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.add;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUk;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartAdd;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * step2
 * 添加购物车 业务校验类
 *    购物车商品合并判断 如果已经存在了 就数量+1
 */
@Component
public class AddCartMergeHandler extends AdapterHandler<TCartAdd> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartAdd inbound) {
        CartItem cart = inbound.getDomain();
        CartItemUk cartItemUk = new CartItemUk(
            cart.getCustId(),
            cart.getCartType(),
            new ItemSkuId(
                cart.getItemId(),
                cart.getSkuId(),
                cart.getSellerId(),
                cart.getSkuQuoteId()
            )
        );
        // 查询是否已经加车
        CartItem existItem = cartService.queryItem(cartItemUk);
        Date now = new Date();
        if (existItem != null) {
            // 合并
            cart.setQuantity(cart.getQuantity() + existItem.getQuantity());
            cart.setId(existItem.getId());
            cart.setGmtModified(now);
        } else {
            // 新增
            cart.setGmtCreate(now);
            cart.setGmtModified(now);
            cart.setAddCartTime(now);
        }
    }
}
