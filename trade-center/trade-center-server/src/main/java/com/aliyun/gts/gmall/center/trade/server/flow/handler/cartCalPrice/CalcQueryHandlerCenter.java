package com.aliyun.gts.gmall.center.trade.server.flow.handler.cartCalPrice;

import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CalCartPriceRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ItemSkuQty;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车商品主表查询
 */
@Component
public class CalcQueryHandlerCenter extends AdapterHandler<TCartCalc> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartCalc inbound) {
        Cart cart = inbound.getDomain();
        // 参数
        CalCartPriceRpcReq calCartPriceRpcReq = inbound.getReq();
        cart.setCustId(calCartPriceRpcReq.getCustId());
        cart.setChannel(calCartPriceRpcReq.getChannel());
        // 根据入参查询是否都在卡里面， 查询
        CartItemUkBatch cartItemUkBatch = new CartItemUkBatch();
        cartItemUkBatch.setCustId(calCartPriceRpcReq.getCustId());
        cartItemUkBatch.setCartType(calCartPriceRpcReq.getCartType());
        cartItemUkBatch.setPayMode(calCartPriceRpcReq.getPayMode());
        cartItemUkBatch.setItemSkuIds(calCartPriceRpcReq.getItemSkuIds().stream().map(sku -> {
            ItemSkuId itemSkuId = new ItemSkuId();
            BeanUtils.copyProperties(sku, itemSkuId);
            return itemSkuId;
        }).collect(Collectors.toList()));

        // 查询购物车卡片信息
        List<CartItem> list = cartService.queryItems(cartItemUkBatch);

        // 购物车查出来的和入参的数量相同， 不能缺少
        if (list.size() != calCartPriceRpcReq.getItemSkuIds().size()) {
            inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
            return;
        }

        // 所有的SKUID
        List<ItemSkuQty> itemSkuIds = calCartPriceRpcReq.getItemSkuIds();
        // 遍历每一个查到的购物车信息，设根据入参设置城市价格 理论上这里不会出现没有城市价的场景 概率很低
        list = list.stream().peek(cartItem->{
            itemSkuIds.stream()
                .filter(p -> p.getSkuId().equals(cartItem.getSkuId()) && p.getSellerId().equals(cartItem.getSellerId()))
                .findAny()
                .ifPresent(itemSkuQty -> cartItem.setSkuQuoteId(itemSkuQty.getSkuQuoteId()));
            itemSkuIds.stream()
                .filter(p -> p.getSkuId().equals(cartItem.getSkuId()) && p.getSellerId().equals(cartItem.getSellerId()))
                .findAny()
                .ifPresent(itemSkuQty -> cartItem.setCityCode(itemSkuQty.getCityCode()));
        }).collect(Collectors.toList());

        /**
         * 商品数量，优先用传入值，缺省用db加购数量
         * 同一个支付方式下面 不会出现相同的SKU 所以按照 item+sku+seller 足以标记唯一性！！！
         */
        Map<ItemSkuId, CartItem> cartItemMap = CommUtils.toMap(list, item -> new ItemSkuId(item.getItemId(), item.getSkuId(), item.getSellerId()));
        calCartPriceRpcReq.getItemSkuIds().stream().forEach(item -> {
            if (item.getQty() != null) {
                cartItemMap.get(new ItemSkuId(
                    item.getItemId(),
                    item.getSkuId(),
                    item.getSellerId())
                )
                .setQuantity(item.getQty());
            }
        });

        // 存为一个分组
        CartGroup group = new CartGroup();
        group.setCartItems(list);
        cart.setGroups(Arrays.asList(group));
        cart.setPayMode(calCartPriceRpcReq.getPayMode());
    }
}
