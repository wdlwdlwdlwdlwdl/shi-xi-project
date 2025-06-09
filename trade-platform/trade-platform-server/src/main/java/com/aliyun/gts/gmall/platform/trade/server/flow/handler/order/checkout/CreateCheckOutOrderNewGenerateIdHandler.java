package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.checkout;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateCheckOutOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.CacheConstants;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.GenerateIdService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CheckOutCreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CacheRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCheckOutCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 订单确认 step 8
 *    生成订单号 用于前端计算使用 ，保存订单的时候重新生成
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateCheckOutOrderNewGenerateIdHandler extends AdapterHandler<TOrderCheckOutCreate> {

    // ID生成工具
    @Autowired
    private GenerateIdService generateIdService;

    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private CacheRepository cacheRepository;

    @Override
    public void handle(TOrderCheckOutCreate inbound) {
        CreateCheckOutOrderRpcReq createCheckOutOrderRpcReq = inbound.getReq();
        Long custId = createCheckOutOrderRpcReq.getCustId();
        CheckOutCreatingOrder checkOutCreatingOrder = inbound.getDomain();
        // 没通过校验 或者已经存入订单 直接跳过
        if (Boolean.FALSE.equals(checkOutCreatingOrder.getCheckSuccess()) ||
            Boolean.TRUE.equals(checkOutCreatingOrder.getCreatedSuccess())) {
            return;
        }
        CreatingOrder creatingOrder = checkOutCreatingOrder.getCreatingOrder();
        List<Long> mainIds = new ArrayList<>();
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            // 生成标品的
            List<Long> ids = generateIdService.nextOrderIds(custId, mainOrder.getSubOrders().size());
            int idx = 0;
            long mainId = ids.get(idx++);
            mainOrder.setPrimaryOrderId(mainId);
            mainIds.add(mainId);
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                subOrder.setOrderId(ids.get(idx++));
                subOrder.setPrimaryOrderId(mainId);
            }
        }
        // 合并下单相互记录id
        if (creatingOrder.getMainOrders().size() > 1) {
            for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
                mainOrder.orderAttr().setMergeOrderIds(mainIds);
            }
        }
        // 设置临时订单号 返回给前端
        creatingOrder.setOriginMainOrderList(mainIds);
        // 临时单号缓存
        cacheRepository.put(String.format(CacheConstants.CREATE_ORINGIN_ORDER_NO, createCheckOutOrderRpcReq.getConfirmOrderToken()), mainIds, 1l, TimeUnit.DAYS);
    }
}



