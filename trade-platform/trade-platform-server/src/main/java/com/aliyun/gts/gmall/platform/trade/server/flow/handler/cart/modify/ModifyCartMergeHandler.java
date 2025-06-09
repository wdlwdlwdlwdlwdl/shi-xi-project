package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.modify;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ModifyCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUk;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartModify;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModifyCartMergeHandler extends AdapterHandler<TCartModify> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartModify inbound) {
        ModifyCartRpcReq modifyCartRpcReq = inbound.getReq();
        // 新的SKUID 用于合单
        if (modifyCartRpcReq.getNewSkuId() == null ||
            modifyCartRpcReq.getNewSkuId().intValue() == modifyCartRpcReq.getSkuId().intValue()) {
            return;
        }
        // 查询旧的 合并购物车
        CartItem cartItem = inbound.getDomain();
        cartItem.setSkuId(modifyCartRpcReq.getNewSkuId());
        CartItemUk cartItemUk = new CartItemUk(
            modifyCartRpcReq.getCustId(),
            modifyCartRpcReq.getCartType(),
            new ItemSkuId(
                modifyCartRpcReq.getItemId(),
                modifyCartRpcReq.getNewSkuId(),
                modifyCartRpcReq.getSellerId()
            )
        );
        CartItem existItem = cartService.queryItem(cartItemUk);
        if (existItem != null) {
            // 合并购物车记录
            cartItem.setQuantity(cartItem.getQuantity() + existItem.getQuantity());
            inbound.putExtra("existItem", existItem);   // 需要删除的记录
        }
    }
}
