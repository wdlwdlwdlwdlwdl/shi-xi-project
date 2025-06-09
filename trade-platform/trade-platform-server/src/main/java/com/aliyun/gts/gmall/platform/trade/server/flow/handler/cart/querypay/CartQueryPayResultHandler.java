package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.querypay;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.converter.CartRpcConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartPayQuery;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CartQueryPayResultHandler extends TradeFlowHandler.AdapterHandler<TCartPayQuery> {

    @Autowired
    private CartRpcConverter cartRpcConverter;

    @Override
    public void handle(TCartPayQuery inbound) {
        Cart cart = inbound.getDomain();
        CartDTO result = cartRpcConverter.toCartDTO(cart);
        log.info("CartQueryPayResultHandler end result={}",result);
        inbound.setResult(result);
    }
}
