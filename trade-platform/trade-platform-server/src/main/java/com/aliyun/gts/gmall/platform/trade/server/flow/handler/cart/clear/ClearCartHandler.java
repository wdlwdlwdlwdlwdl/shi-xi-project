package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.clear;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ClearCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartClear;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClearCartHandler extends AdapterHandler<TCartClear> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartClear inbound) {
        ClearCartRpcReq req = inbound.getReq();
        cartService.clearItems(req.getCustId(), req.getCartType());
    }
}
