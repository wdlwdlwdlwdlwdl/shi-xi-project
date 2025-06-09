package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderCustomerHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void handle(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();

        Customer c = userRepository.getCustomerRequired(req.getCustId());
        for (MainOrder main : order.getMainOrders()) {
            main.setCustomer(c);
        }
    }
}
