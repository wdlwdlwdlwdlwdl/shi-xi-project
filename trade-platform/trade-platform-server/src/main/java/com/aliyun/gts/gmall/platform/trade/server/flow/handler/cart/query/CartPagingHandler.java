package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.query;

import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页截取
 */
@Component
public class CartPagingHandler extends AdapterHandler<TCartQuery> {

    @Override
    public void handle(TCartQuery inbound) {
        Cart cart = inbound.getDomain();
        if (CollectionUtils.isNotEmpty(cart.getGroups()) && CollectionUtils.isNotEmpty(cart.getGroups().get(0).getCartItems())) {
            PageParam page = inbound.getReq().getPage();
            int start = (page.getPageNo() - 1) * page.getPageSize();
            int skipped = 0;
            int remains = page.getPageSize();
            List<CartGroup> newGroups = new ArrayList<>();
            for (CartGroup group : cart.getGroups()) {
                List<CartItem> newItems = new ArrayList<>();
                for (CartItem item : group.getCartItems()) {
                    if (skipped++ < start || remains-- <= 0) {
                        continue;
                    }
                    newItems.add(item);
                }
                if (!newItems.isEmpty()) {
                    group.setCartItems(newItems);
                    newGroups.add(group);
                }
            }
            cart.setGroups(newGroups);
        }
    }
}