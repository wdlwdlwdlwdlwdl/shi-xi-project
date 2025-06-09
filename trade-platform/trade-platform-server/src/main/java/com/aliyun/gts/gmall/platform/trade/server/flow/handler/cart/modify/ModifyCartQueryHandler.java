package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.modify;

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

/**
 * 修改购物车商品数量
 *    查询购物车商品是否存在，差不存在提示
 */
@Component
public class ModifyCartQueryHandler extends AdapterHandler<TCartModify> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartModify inbound) {
        ModifyCartRpcReq modifyCartRpcReq = inbound.getReq();
        // 查询下购物车是否存在
        CartItemUk cartItemUk = new CartItemUk(
            modifyCartRpcReq.getCustId(),
            modifyCartRpcReq.getCartType(),
            new ItemSkuId(
                modifyCartRpcReq.getItemId(),
                modifyCartRpcReq.getSkuId(),
                modifyCartRpcReq.getSellerId()
            )
        );
        CartItem cartItem = cartService.queryItem(cartItemUk);
        if (cartItem == null) {
            inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
            return;
        }
        cartItem.setCityCode(modifyCartRpcReq.getCityCode());
        cartItem.setPayMode(modifyCartRpcReq.getPayMode());
        cartItem.setSkuQuoteId(modifyCartRpcReq.getSkuQuoteId());
        BeanUtils.copyProperties(cartItem, inbound.getDomain());
    }
}
