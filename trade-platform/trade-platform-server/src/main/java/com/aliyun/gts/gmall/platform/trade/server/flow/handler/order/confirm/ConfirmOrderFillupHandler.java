package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConfirmOrderFillupHandler extends AdapterHandler<TOrderConfirm> {

    @Override
    public void handle(TOrderConfirm inbound) {
        // 订单基础填充

        ConfirmOrderInfoRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();

        // 购物车
        order.setIsFromCart(req.getIsFromCart());

        //透传前端扩展参数
        if(req.getParams()!=null) {
            order.addParams(req.getParams());
        }

        // 库存
        Map<Long, Integer> qtyMap = req.getOrderItems().stream()
                .collect(Collectors.toMap(item -> item.getSkuId(), item -> item.getItemQty()));

        for (MainOrder main : order.getMainOrders()) {
            // channel
            main.setOrderChannel(req.getOrderChannel());

            // custId
            Customer customer = new Customer();
            customer.setCustId(req.getCustId());
            main.setCustomer(customer);

            // 库存
            for (SubOrder sub : main.getSubOrders()) {
                sub.setOrderQty(qtyMap.get(sub.getItemSku().getSkuId()));
            }
        }
    }
}
