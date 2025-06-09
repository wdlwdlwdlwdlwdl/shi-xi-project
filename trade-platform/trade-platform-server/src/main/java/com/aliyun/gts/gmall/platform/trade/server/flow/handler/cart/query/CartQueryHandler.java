package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.query;

import cn.hutool.core.collection.CollectionUtil;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 购物车商品主表查询
 */
@Component
public class CartQueryHandler extends AdapterHandler<TCartQuery> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartQuery inbound) {
        Cart cart = inbound.getDomain();
        Long custId = inbound.getReq().getCustId();
        Integer cartType = inbound.getReq().getCartType();
        // 查询全部购物车信息
        List<CartItem> list = cartService.queryAll(custId, cartType);
        // 填值
        cart.setCustId(custId);
        cart.setChannel(inbound.getReq().getChannel());
        cart.setPromotionSource(inbound.getReq().getPromotionSource());
        // 设置参数
        if (CollectionUtil.isNotEmpty(list)) {
            list.stream().forEach(cartItem -> {
                cartItem.setCityCode(inbound.getReq().getCityCode());
                cartItem.setOriginalPayMode(cartItem.getPayMode());
            });
        }
        // 分组由后续节点处理, 这里存为一个分组
        CartGroup group = new CartGroup();
        group.setCartItems(list);
        cart.setGroups(List.of(group));
        cart.setTotalItemCount(list.size());
        if (CollectionUtil.isNotEmpty(list)) {
            cart.setTotalItemCount(list.stream().map(cartItem -> cartItem.getQuantity()).reduce(Integer::sum).get());
        }
    }
}
