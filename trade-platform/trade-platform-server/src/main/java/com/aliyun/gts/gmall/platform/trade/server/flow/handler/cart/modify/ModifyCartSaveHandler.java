package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.modify;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.modify.CartModifyResultDTO;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.converter.CartRpcConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartModify;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 修改购物车商品数量 业务校验类
 *    修改商品数量
 *    修改加车价格
 */
@Component
public class ModifyCartSaveHandler extends AdapterHandler<TCartModify> {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRpcConverter cartRpcConverter;

    @Override
    public void handle(TCartModify inbound) {
        CartItem cartItem = inbound.getDomain();
        CartModifyResultDTO result = new CartModifyResultDTO();
        CartItem existItem = (CartItem) inbound.getExtra("existItem");
        // cartItem.setAddCartTime(new Date());
        if (existItem != null) {
            cartService.save(cartItem, existItem.getId());
            result.setSkuMerged(true);
        } else {
            cartService.save(cartItem);
        }
        result.setQuantity(cartItem.getQuantity());
        result.setCartItem(this.cartRpcConverter.toCartItemDTO(cartItem));
        inbound.setResult(result);
    }
}
