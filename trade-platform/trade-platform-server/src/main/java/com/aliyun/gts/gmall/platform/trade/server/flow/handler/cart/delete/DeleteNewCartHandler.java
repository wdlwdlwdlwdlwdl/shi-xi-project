package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.delete;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.DeleteCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartDelete;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class DeleteNewCartHandler extends TradeFlowHandler.AdapterHandler<TCartDelete> {

    @Autowired
    private CartService cartService;


    @Override
    public void handle(TCartDelete inbound) {
        DeleteCartRpcReq req = inbound.getReq();
        List<Long> cartIds=  req.getItemSkuIds().stream().map(ItemSkuId::getCartId).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(cartIds)){
            return;
        }
        /**购物车通过IDS直接删除 不能通过查询再删 因为支付方式会变 可能删的不对！！*/
        cartService.deleteItems(cartIds);
    }
}
