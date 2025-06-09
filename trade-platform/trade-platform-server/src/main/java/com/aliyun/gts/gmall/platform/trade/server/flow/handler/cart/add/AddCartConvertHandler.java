package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.add;

import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.converter.CartRpcConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartAdd;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * step1
 *  添加购物车 入参数据转换
 */
@Component
public class AddCartConvertHandler extends AdapterHandler<TCartAdd> {

    @Autowired
    private CartRpcConverter cartRpcConverter;

    @Override
    public void handle(TCartAdd inbound) {
        // 入参转换
        CartItem cart = cartRpcConverter.toCartItem(inbound.getReq());
        // 塞进流程对象
        BeanUtils.copyProperties(cart, inbound.getDomain());
    }
}
