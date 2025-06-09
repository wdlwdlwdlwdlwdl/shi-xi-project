package com.aliyun.gts.gmall.center.trade.server.flow.handler.cartModify;

import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ModifyCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUk;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartModify;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModifyCartQueryHandlerCenter extends AdapterHandler<TCartModify> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartModify inbound) {
        ModifyCartRpcReq modifyCartRpcReq = inbound.getReq();
        CartItemUk cartItemUk = new CartItemUk(
            modifyCartRpcReq.getCustId(),
            modifyCartRpcReq.getCartType(),
            new ItemSkuId(
                modifyCartRpcReq.getItemId(),
                modifyCartRpcReq.getSkuId(),
                modifyCartRpcReq.getSellerId(),
                modifyCartRpcReq.getSkuQuoteId(),
                modifyCartRpcReq.getCityCode()
            ),
            modifyCartRpcReq.getSellerId(),
            modifyCartRpcReq.getPayMode()
        );
        // 修改数量前提必须已经在购物车中
        CartItem cartItem = cartService.queryItem(cartItemUk);
        if (cartItem == null) {
            inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
            return;
        }
        // 城市code
        cartItem.setCityCode(modifyCartRpcReq.getCityCode());
        BeanUtils.copyProperties(cartItem, inbound.getDomain());
    }
}
