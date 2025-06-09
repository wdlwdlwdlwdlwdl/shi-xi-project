package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.delete;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.DeleteCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUkBatch;
import com.aliyun.gts.gmall.platform.trade.server.converter.CartRpcConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartDelete;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeleteCartHandler extends AdapterHandler<TCartDelete> {

    @Autowired
    private CartService cartService;
    @Autowired
    private CartRpcConverter cartRpcConverter;

    @Override
    public void handle(TCartDelete inbound) {
        DeleteCartRpcReq req = inbound.getReq();
        CartItemUkBatch ukBatch = cartRpcConverter.toCartItemUkBatch(req);
        cartService.deleteItems(ukBatch);
    }
}
