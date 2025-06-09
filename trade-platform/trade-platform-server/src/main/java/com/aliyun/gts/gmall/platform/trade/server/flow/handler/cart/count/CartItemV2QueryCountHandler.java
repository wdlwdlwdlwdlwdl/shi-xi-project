package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.count;

import cn.hutool.core.collection.CollectionUtil;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.QueryCartItemQuantityRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCartDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartCount;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartItemV2QueryCountHandler extends AdapterHandler<TCartCount> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TCartCount inbound) {
        QueryCartItemQuantityRpcReq req = inbound.getReq();
        int count = 0;
        Long custId = req.getCustId();
        Integer cartType = req.getCartType();
        // 查询全部购物车信息
        List<CartItem> list = cartService.queryAll(custId, cartType);
        if (CollectionUtil.isNotEmpty(list)) {
            count = list.stream().map(TcCartDO::getQuantity).reduce(Integer::sum).get();
        }
        inbound.setResult(count);
    }
}
