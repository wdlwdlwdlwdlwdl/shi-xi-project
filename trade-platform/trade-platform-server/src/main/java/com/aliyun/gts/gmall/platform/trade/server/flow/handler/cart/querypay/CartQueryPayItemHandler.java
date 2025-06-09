package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.querypay;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartFillService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartPayQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CartQueryPayItemHandler extends TradeFlowHandler.AdapterHandler<TCartPayQuery> {

    @Autowired
    private CartFillService cartFillService;

    @Override
    public void handle(TCartPayQuery inbound) {
        Cart cart = inbound.getDomain();
        if (CollectionUtils.isNotEmpty(cart.getGroups())) {
            // 补全商品信息
            cartFillService.fillItemInfo(cart);
        }
    }

}
