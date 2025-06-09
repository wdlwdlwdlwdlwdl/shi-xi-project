package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.query;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 内存排序分组 处理
 */
@Component
public class CartGroupingHandler extends AdapterHandler<TCartQuery> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartQuery inbound) {
        Cart cart = inbound.getDomain();
        if (CollectionUtils.isNotEmpty(cart.getGroups())) {
            cartService.sortGrouping(cart);
        }
    }
}
