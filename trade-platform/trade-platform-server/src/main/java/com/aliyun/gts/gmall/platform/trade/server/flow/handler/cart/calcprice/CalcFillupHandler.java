package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.calcprice;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartFillService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartCalc;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalcFillupHandler extends AdapterHandler<TCartCalc> {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartFillService cartFillService;

    @Override
    public void handle(TCartCalc inbound) {
        Cart cart = inbound.getDomain();
        // 填充商品信息
        cartFillService.fillItemInfo(cart);
        // 分组计算
        cartService.sortGrouping(cart);
    }
}
