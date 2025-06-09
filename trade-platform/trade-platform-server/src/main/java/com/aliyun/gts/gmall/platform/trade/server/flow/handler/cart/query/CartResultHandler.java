package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.query;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.converter.CartRpcConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 购物车查询结果转换
 */
@Slf4j
@Component
public class CartResultHandler extends AdapterHandler<TCartQuery> {

    @Autowired
    private CartRpcConverter cartRpcConverter;

    @Override
    public void handle(TCartQuery inbound) {
        Cart cart = inbound.getDomain();
        CartDTO result = cartRpcConverter.toCartDTO(cart);
        log.info("CartResultHandler end result={}",result);
        inbound.setResult(result);
    }
}
