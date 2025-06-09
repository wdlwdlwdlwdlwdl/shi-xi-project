package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.query;

import cn.hutool.core.collection.CollectionUtil;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartItemDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.converter.CartRpcConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartSingleQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 购物车查询结果转换
 */
@Component
public class CartSingleResultHandler extends AdapterHandler<TCartSingleQuery> {

    @Autowired
    private CartRpcConverter cartRpcConverter;

    @Override
    public void handle(TCartSingleQuery inbound) {
        Cart cart = inbound.getDomain();
        if (CollectionUtil.isNotEmpty(cart.getGroups()) && cart.getGroups().get(0) != null) {
            CartGroup group = cart.getGroups().get(0);
            if (CollectionUtil.isNotEmpty(group.getCartItems()) && group.getCartItems().get(0) != null) {
                CartItem cartItem = cart.getGroups().get(0).getCartItems().get(0);
                CartItemDTO result = cartRpcConverter.toCartItemDTO(cartItem);
                inbound.setResult(result);
                return;
            }
        }
        inbound.setResult(new CartItemDTO());
    }
}
