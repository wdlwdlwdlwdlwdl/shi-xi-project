package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.calcprice;

import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CalCartPriceRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItemUkBatch;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartCalc;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车商品主表查询
 */
@Component
public class CalcQueryHandler extends AdapterHandler<TCartCalc> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartCalc inbound) {
        Cart cart = inbound.getDomain();
        // 入参转换
        CalCartPriceRpcReq calCartPriceRpcReq = inbound.getReq();
        cart.setCustId(calCartPriceRpcReq.getCustId());
        cart.setChannel(calCartPriceRpcReq.getChannel());
        CartItemUkBatch cartItemUkBatch = new CartItemUkBatch();
        cartItemUkBatch.setCustId(calCartPriceRpcReq.getCustId());
        cartItemUkBatch.setCartType(calCartPriceRpcReq.getCartType());
        cartItemUkBatch.setItemSkuIds(
            calCartPriceRpcReq.getItemSkuIds().stream().map(sku -> {
                ItemSkuId itemSkuId = new ItemSkuId();
                BeanUtils.copyProperties(sku, itemSkuId);
                return itemSkuId;
            }
        )
        .collect(Collectors.toList()));
        // 查询选择的商品 否在都在购物车里面
        List<CartItem> list = cartService.queryItems(cartItemUkBatch);
        // 数量不相同 提示错误
        if (list.size() != calCartPriceRpcReq.getItemSkuIds().size()) {
            inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
            return;
        }
        // 商品数量，优先用传入值，缺省用db加购数量
        Map<ItemSkuId, CartItem> itemSkuIdCartItemMap = CommUtils.toMap(list, item ->
            new ItemSkuId(
                item.getItemId(),
                item.getSkuId(),
                item.getSellerId()
            )
        );
        calCartPriceRpcReq.getItemSkuIds().stream().forEach(item -> {
            if (item.getQty() != null) {
                itemSkuIdCartItemMap.get(new ItemSkuId(
                    item.getItemId(),
                    item.getSkuId())
                )
                .setQuantity(item.getQty());
            }
        });
        // 存为一个分组
        CartGroup group = new CartGroup();
        group.setCartItems(list);
        cart.setGroups(List.of(group));
    }
}
