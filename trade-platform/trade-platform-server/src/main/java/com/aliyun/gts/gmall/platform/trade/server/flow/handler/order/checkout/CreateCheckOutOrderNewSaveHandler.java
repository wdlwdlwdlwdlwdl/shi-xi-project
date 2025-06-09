package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.checkout;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateCheckOutOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.CacheConstants;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.*;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CacheRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCheckOutCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *  生成结算单流程
 *
 * @anthor shifeng
 * @version 1.0.1
 * 2025-1-6 14:42:57
 */
@Component
public class CreateCheckOutOrderNewSaveHandler extends TradeFlowHandler.AdapterHandler<TOrderCheckOutCreate> {

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private OrderCreateService orderCreateService;

    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;

    @Override
    public void handle(TOrderCheckOutCreate inbound) {
        CreateCheckOutOrderRpcReq createCheckOutOrderRpcReq = inbound.getReq();
        CheckOutCreatingOrder checkOutCreatingOrder = inbound.getDomain();
        // 没通过校验 或者已经存入订单 直接跳过
        if (Boolean.FALSE.equals(checkOutCreatingOrder.getCheckSuccess()) ||
            Boolean.TRUE.equals(checkOutCreatingOrder.getCreatedSuccess())) {
            return;
        }
        CreatingOrder creatingOrder = checkOutCreatingOrder.getCreatingOrder();
        // 订单保存
        orderCreateService.saveOrder(creatingOrder);

        // 写流水
        List<TcOrderOperateFlowDO> tcOrderOperateFlowDOs = new ArrayList<>();
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            TcOrderOperateFlowDO tcOrderOperateFlowDO= getOpFlow(mainOrder, OrderChangeOperateEnum.CUST_CREATE_ORDER);
            tcOrderOperateFlowDOs.add(tcOrderOperateFlowDO);
        }
        if(CollectionUtils.isNotEmpty(tcOrderOperateFlowDOs))
        {
            // 写流水
            tcOrderOperateFlowRepository.batchCreate(tcOrderOperateFlowDOs);
        }
        // 幂等ID check
        String redisKey =  String.format(CacheConstants.CREATE_CHECK_ORDER, createCheckOutOrderRpcReq.getConfirmOrderToken());
        cacheRepository.put(redisKey, "1", 1L, TimeUnit.DAYS);
        checkOutCreatingOrder.setCreatedSuccess(Boolean.TRUE);
    }

    private TcOrderOperateFlowDO getOpFlow(MainOrder mainOrder, OrderChangeOperate op) {

        TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
        flow.setOrderId(mainOrder.getPrimaryOrderId());
        flow.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        flow.setToOrderStatus(mainOrder.getPrimaryOrderStatus());
        flow.setOperator(String.valueOf(mainOrder.getCustomer().getCustId()));
        flow.setOperatorType(op.getOprType());
        flow.setOpName(op.getOpName());
        return flow;
    }
}
