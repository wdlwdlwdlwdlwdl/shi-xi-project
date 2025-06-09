package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 订单创建 step 16
 *     下单异常场景计算处理
 *     如果下单失败，则回滚占用的资源
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewDelCartHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private CartService cartService;

    @Override
    public void handle(TOrderCreate inbound) {
        // 入参
        CreateOrderRpcReq createOrderRpcReq = inbound.getReq();
        if (CollectionUtils.isEmpty(createOrderRpcReq.getDeleteCartIds())){
            return;
        }
        CreatingOrder creatingOrder = inbound.getDomain();
        // 非购物车下单
        if (Objects.isNull(creatingOrder.getIsFromCart()) ||
            Boolean.FALSE.equals(creatingOrder.getIsFromCart())) {
            return;
        }
        /**购物车通过IDS直接删除 不能通过查询再删 因为支付方式会变 可能删的不对！！*/
        cartService.deleteItems(createOrderRpcReq.getDeleteCartIds());
    }
}
