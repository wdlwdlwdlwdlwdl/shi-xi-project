package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.count;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.QueryCartItemQuantityRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartCount;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartQueryCountHandler extends AdapterHandler<TCartCount> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartCount inbound) {
        QueryCartItemQuantityRpcReq req = inbound.getReq();
        int count = cartService.queryCount(req.getCustId(), req.getCartType());
        inbound.setResult(count);
    }
}
