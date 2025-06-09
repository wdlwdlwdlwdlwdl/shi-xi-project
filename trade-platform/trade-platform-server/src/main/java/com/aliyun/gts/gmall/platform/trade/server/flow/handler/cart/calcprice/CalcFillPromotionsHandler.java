package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.calcprice;

import com.aliyun.gts.gmall.platform.trade.api.constant.PayMode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartFillService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartCalc;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 勾选商品计算营销
 */
@Component
public class CalcFillPromotionsHandler extends TradeFlowHandler.AdapterHandler<TCartCalc> {

    @Autowired
    private CartFillService cartFillService;

    @Override
    public void handle(TCartCalc inbound) {
        Cart cart = inbound.getDomain();
        // 根据分组一个个计算，不能一起计算
        if (CollectionUtils.isEmpty(cart.getGroups())) {
            return;
        }
        cartFillService.fillCartGroupItemPromotions(cart);
        // 计算数据
        cart.getGroups().stream().forEach(cartGroup -> {
            if(!PayMode.INVALID.equals(cartGroup.getPayMode())) {
                cartGroup.calcTotal();
            }
        });
    }
}