package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.checkout;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateCheckOutOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CheckOutCreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCheckOutCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateCheckOutOrderCustomerHandler extends TradeFlowHandler.AdapterHandler<TOrderCheckOutCreate> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void handle(TOrderCheckOutCreate inbound) {
        // 入参
        CreateCheckOutOrderRpcReq createCheckOutOrderRpcReq = inbound.getReq();
        CheckOutCreatingOrder checkOutCreatingOrder = inbound.getDomain();
        // 没通过校验 或者已经存入订单 直接跳过
        if (Boolean.FALSE.equals(checkOutCreatingOrder.getCheckSuccess()) ||
            Boolean.TRUE.equals(checkOutCreatingOrder.getCreatedSuccess())) {
            return;
        }
        Customer customer = userRepository.getCustomerRequired(createCheckOutOrderRpcReq.getCustId());
        for (MainOrder mainOrder : checkOutCreatingOrder.getCreatingOrder().getMainOrders()) {
            mainOrder.setCustomer(customer);
            mainOrder.setFirstName(customer.getFirstName());
            mainOrder.setLastName(customer.getLastName());
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                subOrder.setFirstName(customer.getFirstName());
                subOrder.setLastName(customer.getLastName());
            }
        }
    }
}
