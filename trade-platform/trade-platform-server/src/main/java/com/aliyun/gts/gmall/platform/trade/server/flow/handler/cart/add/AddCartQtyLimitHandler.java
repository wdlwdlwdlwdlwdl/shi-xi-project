package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.add;

import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartAdd;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 添加购物车 业务校验类
 *    购物车商品数量上线判断
 */
@Component
public class AddCartQtyLimitHandler extends AdapterHandler<TCartAdd> {

    @Value("${trade.center.cart.limitCount:50}")
    private Integer limitCount;

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartAdd inbound) {
        CartItem cart = inbound.getDomain();
        // 判断当前用户加入购物车的数量集合
        if (Objects.isNull(cart.getId()) && Objects.nonNull(limitCount)) {
            // 查询 当前用户的加车数量
            int count = cartService.queryCount(cart.getCustId(), cart.getCartType());
            // 达到上线 不可以再加车了
            if (count >= limitCount.intValue()) {
                inbound.setError(CartErrorCode.CART_QTY_OUT_LIMIT);
            }
        }
    }
}
