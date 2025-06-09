package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.modify;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ModifyCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ItemRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartModify;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModifyCartQtyHandler extends AdapterHandler<TCartModify> {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public void handle(TCartModify inbound) {
        ModifyCartRpcReq modifyCartRpcReq = inbound.getReq();
        CartItem cartItem = inbound.getDomain();
        if (modifyCartRpcReq.getNewItemQty() != null &&
            modifyCartRpcReq.getNewItemQty().intValue() != cartItem.getQuantity().intValue()) {
            cartItem.setQuantity(modifyCartRpcReq.getNewItemQty());
        }
    }
}
