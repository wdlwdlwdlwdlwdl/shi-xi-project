package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

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

/**
 * 订单创建 step 11
 *    根据规则生成ID
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewGenerateIdHandler extends AdapterHandler<TOrderCreate> {

    // ID生成工具
    @Autowired
    private GenerateIdService generateIdService;

    @Override
    public void handle(TOrderCreate inbound) {
        Long custId = inbound.getReq().getCustId();
        CreatingOrder creatingOrder = inbound.getDomain();
        List<Long> mainIds = new ArrayList<>();
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            // 生成标品的
            List<Long> ids = generateIdService.nextOrderIds(custId, mainOrder.getSubOrders().size());
            int idx = 0;
            long mainId = ids.get(idx++);
            mainOrder.setPrimaryOrderId(mainId);
            mainIds.add(mainId);
            //生成对外展示id
            String mainDisplayOrderId = generateIdService.nextDisplayOrderIds();
            mainOrder.setDisplayOrderId(mainDisplayOrderId);
            int subIdx = 1;
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                subOrder.setOrderId(ids.get(idx++));
                subOrder.setPrimaryOrderId(mainId);
                subOrder.setDisplayOrderId(mainDisplayOrderId + "-" + subIdx++);
            }

        }
        // 合并下单相互记录id
        if (creatingOrder.getMainOrders().size() > 1) {
            for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
                mainOrder.orderAttr().setMergeOrderIds(mainIds);
            }
        }
    }
}
