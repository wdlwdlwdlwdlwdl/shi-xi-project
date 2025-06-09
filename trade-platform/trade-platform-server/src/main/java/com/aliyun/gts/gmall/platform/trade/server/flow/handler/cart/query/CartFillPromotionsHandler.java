package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.query;

import com.aliyun.gts.gmall.platform.trade.api.constant.PayMode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartFillService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCart;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartFillPromotionsHandler extends TradeFlowHandler.AdapterHandler<TCart<?, ?>> {

    @Autowired
    private CartFillService cartFillService;

    @Override
    public void handle(TCart<?, ?> inbound) {
        Cart cart = inbound.getDomain();
        // 根据分组一个个计算，不能一起计算
        if (CollectionUtils.isEmpty(cart.getGroups())) {
            return;
        }
        // 营销计算
        cartFillService.fillCartGroupItemPromotions(cart);
        // 计算数据
        cart.getGroups().stream().forEach(cartGroup -> {
            if(!PayMode.INVALID.equals(cartGroup.getPayMode())) {
                cartGroup.calcTotal();
            }
        });
    }
}
