package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.querypay;

import cn.hutool.core.collection.CollectionUtil;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartPayQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.CartGroupTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartPayQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 根据支付方式查询购物车
 * @anthor shifeng
 * @version 1.0.1
 * 2025-2-14 17:34:38
 */
@Slf4j
@Component
public class CartQueryPayModeHandler extends TradeFlowHandler.AdapterHandler<TCartPayQuery> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartPayQuery inbound) {
        Cart cart = inbound.getDomain();
        CartPayQueryRpcReq cartPayQueryRpcReq = inbound.getReq();
        // 查询全部购物车信息
        List<CartItem> list = cartService.queryPayMode(
            cartPayQueryRpcReq.getCustId(),
            cartPayQueryRpcReq.getCartType(),
            cartPayQueryRpcReq.getPayMode()
        );
        // 填值
        cart.setCustId(cartPayQueryRpcReq.getCustId());
        cart.setChannel(inbound.getReq().getChannel());
        cart.setPromotionSource(inbound.getReq().getPromotionSource());
        // 设置参数
        if (CollectionUtil.isNotEmpty(list)) {
            list.stream().forEach(cartItem -> cartItem.setCityCode(inbound.getReq().getCityCode()));
        }
        // 分组由后续节点处理, 这里存为一个分组
        CartGroup group = new CartGroup();
        group.setCartItems(list);
        group.setPayMode(cartPayQueryRpcReq.getPayMode());
        group.setGroupType(CartGroupTypeEnum.PAY_MODE_GROUP.getCode());
        cart.setGroups(List.of(group));
        cart.setTotalItemCount(list.size());
        if (CollectionUtil.isNotEmpty(list)) {
            cart.setTotalItemCount(list.stream().map(cartItem -> cartItem.getQuantity()).reduce(Integer::sum).get());
        }
    }
}
