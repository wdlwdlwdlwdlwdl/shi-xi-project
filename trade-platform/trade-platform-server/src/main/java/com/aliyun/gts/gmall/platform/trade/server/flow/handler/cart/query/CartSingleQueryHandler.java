package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.query;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartSingleQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUk;
import com.aliyun.gts.gmall.platform.trade.server.converter.CartRpcConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartSingleQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 购物车商品主表查询
 */
@Component
public class CartSingleQueryHandler extends AdapterHandler<TCartSingleQuery> {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRpcConverter cartRpcConverter;

    @Override
    public void handle(TCartSingleQuery inbound) {
        // 入参
        CartSingleQueryRpcReq cartSingleQueryRpcReq =  inbound.getReq();
        // 填值
        Cart cart = inbound.getDomain();
        // 数据转换
        CartItemUk cartItemUk = cartRpcConverter.toCartItemUk(inbound.getReq());
        // 购物车查询 并提供城市信息和支付方式
        CartItem cartItem = cartService.queryItem(cartItemUk);
        cartItem.setPayMode(cartSingleQueryRpcReq.getPayMode());
        cartItem.setCityCode(cartSingleQueryRpcReq.getCityCode());

        // 购物车分组
        CartGroup group = new CartGroup();
        group.setCartItems(Collections.singletonList(cartItem));

        // 参数设置
        cart.setTotalItemCount(1);
        cart.setGroups(List.of(group));
        cart.setCustId(cartItemUk.getCustId());
        cart.setChannel(cartSingleQueryRpcReq.getChannel());
        cart.setPromotionSource(cartSingleQueryRpcReq.getPromotionSource());
    }
}
