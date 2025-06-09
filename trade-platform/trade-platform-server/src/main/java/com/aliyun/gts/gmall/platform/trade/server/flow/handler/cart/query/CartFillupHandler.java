package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.query;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartFillService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCart;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 商品信息补全
 */
@Component
public class CartFillupHandler extends AdapterHandler<TCart<?, ?>> {

    @Autowired
    private CartFillService cartFillService;

    @Override
    public void handle(TCart<?, ?> inbound) {
        Cart cart = inbound.getDomain();
        if (CollectionUtils.isNotEmpty(cart.getGroups()) &&
            Objects.nonNull(cart.getGroups().get(0)) &&
            CollectionUtils.isNotEmpty(cart.getGroups().get(0).getCartItems())) {
            // 补全商品信息
            cartFillService.fillItemInfo(cart);
            // 补全营销信息
            // cartFillService.fillItemPromotions(cart);
        }
    }
}
