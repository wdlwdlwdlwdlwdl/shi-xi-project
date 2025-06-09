package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.add;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartAdd;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 添加购物车 业务校验类
 *     加车保存
 */
@Component
public class AddCartSaveHandler extends AdapterHandler<TCartAdd> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartAdd inbound) {
        cartService.save(inbound.getDomain());
    }
}
