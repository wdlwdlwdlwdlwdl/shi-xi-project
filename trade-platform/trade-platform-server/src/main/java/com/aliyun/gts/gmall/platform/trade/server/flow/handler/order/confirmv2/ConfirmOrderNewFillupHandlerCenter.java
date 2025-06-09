package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderExtendsAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderFeatureAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderExtendConverter;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ConfirmOrderNewFillupHandlerCenter extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private OrderFeatureAbility orderFeatureAbility;

    @Autowired
    private OrderExtendsAbility orderExtendsAbility;

    @Autowired
    private OrderExtendConverter orderExtendConverter;

    @Override
    public void handle(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq = inbound.getReq();
        CreatingOrder creatingOrder = inbound.getDomain();
        //下单常量、调用参数填充
        Date now = new Date();
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            mainOrder.setGmtCreate(now);
            mainOrder.setGmtModified(now);
            mainOrder.setPrimaryOrderStatus(OrderStatusEnum.CREATED.getCode());
            // 订单属性
            mainOrder.orderAttr().setOrderType(mainOrder.getOrderType());
            mainOrder.orderAttr().setPayType(confirmOrderInfoRpcReq.getPayMode());
            mainOrder.orderAttr().setOrderStage(OrderStageEnum.BEFORE_SALE.getCode());
            mainOrder.orderAttr().setPayChannel(confirmOrderInfoRpcReq.getPayChannel());
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                subOrder.setGmtCreate(now);
                subOrder.setGmtModified(now);
                subOrder.setOrderStatus(OrderStatusEnum.CREATED.getCode());
                subOrder.orderAttr().setOrderStage(OrderStageEnum.BEFORE_SALE.getCode());
                subOrder.orderAttr().setPayType(confirmOrderInfoRpcReq.getPayMode());
            }
        }
        // 支付的请求参数
        creatingOrder.setPayChannel(confirmOrderInfoRpcReq.getPayChannel());
        // 下单打 feature、tag、扩展结构 的扩展逻辑，同时进行元数据校验（含调用方写入的扩展）
        orderFeatureAbility.addTagsOnCrete(creatingOrder);
        // 扩展写入
        orderExtendsAbility.addExtendOnCrete(creatingOrder);
    }
}
