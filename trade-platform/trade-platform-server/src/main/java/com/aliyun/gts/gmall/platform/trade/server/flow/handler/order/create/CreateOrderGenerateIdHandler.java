package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.GenerateIdService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CreateOrderGenerateIdHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private GenerateIdService generateIdService;

    @Override
    public void handle(TOrderCreate inbound) {
        Long custId = inbound.getReq().getCustId();
        CreatingOrder order = inbound.getDomain();

        List<Long> mainIds = new ArrayList<>();
        for (MainOrder main : order.getMainOrders()) {
            List<Long> ids = generateIdService.nextOrderIds(custId, main.getSubOrders().size());
            int idx = 0;
            long mainId = ids.get(idx++);
            main.setPrimaryOrderId(mainId);
            mainIds.add(mainId);
            for (SubOrder sub : main.getSubOrders()) {
                sub.setOrderId(ids.get(idx++));
                sub.setPrimaryOrderId(mainId);
            }
        }

        // 合并下单相互记录id
        if (order.getMainOrders().size() > 1) {
            for (MainOrder main : order.getMainOrders()) {
                main.orderAttr().setMergeOrderIds(mainIds);
            }
        }
    }
}
