package com.aliyun.gts.gmall.center.trade.server.flow.handler.orderCreate;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.OrderGroupInfo;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderExtendsAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderFeatureAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderExtendConverter;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class CreateOrderFillupHandlerCenter extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderFeatureAbility orderFeatureAbility;

    @Autowired
    private OrderExtendsAbility orderExtendsAbility;

    @Autowired
    private OrderExtendConverter orderExtendConverter;

    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();

        // 下单常量、调用参数填充
        Date now = new Date();
        Map<Long, OrderGroupInfo> reqMap = CommUtils.toMap(req.getOrderInfos(), OrderGroupInfo::getSellerId);
        for (MainOrder main : order.getMainOrders()) {
            main.setPrimaryOrderStatus(OrderStatusEnum.CREATED.getCode());
            main.setGmtCreate(now);
            main.setGmtModified(now);
            main.orderAttr().setOrderStage(OrderStageEnum.BEFORE_SALE.getCode());
            main.orderAttr().setOrderType(main.getOrderType());
            main.orderAttr().setPayChannel(req.getPayChannel());

            OrderGroupInfo group = reqMap.get(main.getSeller().getSellerId());
            if (group != null) {
                main.setCustMemo(group.getRemark());
                // 调用方写入的扩展
                main.orderAttr().putAllExtend(group.getExtraFeature());
                main.getOrderExtendList().addAll(orderExtendConverter.toExtendList(group.getExpendStruct()));
            }

            for (SubOrder sub : main.getSubOrders()) {
                sub.setOrderStatus(OrderStatusEnum.CREATED.getCode());
                sub.orderAttr().setOrderStage(OrderStageEnum.BEFORE_SALE.getCode());
                sub.setGmtCreate(now);
                sub.setGmtModified(now);
            }
        }

        // 支付的请求参数
        order.setPayChannel(req.getPayChannel());


        // 下单打 feature、tag、扩展结构 的扩展逻辑，同时进行元数据校验（含调用方写入的扩展）
        orderFeatureAbility.addFeatureOnCrete(order);
        orderFeatureAbility.addTagsOnCrete(order);
        orderExtendsAbility.addExtendOnCrete(order);
    }
}
