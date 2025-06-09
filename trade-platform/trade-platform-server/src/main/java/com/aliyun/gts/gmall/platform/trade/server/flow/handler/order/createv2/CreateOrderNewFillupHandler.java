package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.OrderGroupInfo;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStageEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderExtendsAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderFeatureAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderExtendConverter;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 订单创建 step 10
 *    订单信息完善补充
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewFillupHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderExtendConverter orderExtendConverter;

    // 订单备用字段扩展点 写到 tc_order 表 feature
    @Autowired
    private OrderFeatureAbility orderFeatureAbility;

    // 订单扩展字段扩展点 扩展到 tc_order_extend
    @Autowired
    private OrderExtendsAbility orderExtendsAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq createOrderRpcReq = inbound.getReq();
        CreatingOrder creatingOrder = inbound.getDomain();
        // 下单常量、调用参数填充
        Date now = new Date();
        Map<Long, OrderGroupInfo> reqMap = new HashMap<>();
        for(OrderGroupInfo orderGroupInfo: createOrderRpcReq.getOrderInfos())
        {
            reqMap.put(orderGroupInfo.getSellerId(), orderGroupInfo);
        }
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            // 主单数据
            mainOrder.setGmtCreate(now);
            mainOrder.setGmtModified(now);
            mainOrder.setPrimaryOrderStatus(OrderStatusEnum.CREATED.getCode());
            mainOrder.orderAttr().setOrderStage(OrderStageEnum.BEFORE_SALE.getCode());
            mainOrder.orderAttr().setOrderType(mainOrder.getOrderType());
            mainOrder.orderAttr().setPayChannel(createOrderRpcReq.getPayChannel());
            mainOrder.orderAttr().setPayType(createOrderRpcReq.getPayMode());
            mainOrder.orderAttr().setBankCardNbr(createOrderRpcReq.getBankCardNbr());
            OrderGroupInfo group = reqMap.get(mainOrder.getSeller().getSellerId());
            if (group != null) {
                mainOrder.setCustMemo(group.getRemark());
                // 调用方写入的扩展
                mainOrder.orderAttr().putAllExtend(group.getExtraFeature());
                mainOrder.getOrderExtendList().addAll(orderExtendConverter.toExtendList(group.getExpendStruct()));
            }
            // 子单数据
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                subOrder.setGmtCreate(now);
                subOrder.setGmtModified(now);
                subOrder.setOrderStatus(OrderStatusEnum.CREATED.getCode());
                subOrder.orderAttr().setOrderStage(OrderStageEnum.BEFORE_SALE.getCode());
                subOrder.orderAttr().setPayType(createOrderRpcReq.getPayMode());
                subOrder.orderAttr().setBankCardNbr(createOrderRpcReq.getBankCardNbr());
                subOrder.orderAttr().setSkuQuoteId(subOrder.getSkuQuoteId());
                boolean isPresent = Optional.ofNullable(subOrder.getItemSku())
                        .map(ItemSku::getSeller)
                        .isPresent();
                if (isPresent) {
                    subOrder.orderAttr().setSellerPhone(subOrder.getItemSku().getSeller().getPhone());
                }
                subOrder.orderAttr().setMerchantSkuCode(subOrder.getItemSku().getMerchantSkuCode());
            }
        }
        /**
         * 支付的请求参数
         */
        creatingOrder.setPayChannel(createOrderRpcReq.getPayChannel());
        /**
         * 下单打 feature、tag、扩展结构 的扩展逻辑，同时进行元数据校验（含调用方写入的扩展）
         * 基础实现 DefaultOrderFeatureExt
         * 扩展实现 MatchAllOrderFeatureExt
         * 虚拟商品实现 EvoucherOrderFeatureExt
         */
        orderFeatureAbility.addFeatureOnCrete(creatingOrder);
        /**
         * 下单打 tag (tag 进搜索) 哈利克不用
         * 基础实现 DefaultOrderFeatureExt
         * 扩展实现 MatchAllOrderFeatureExt
         */
        orderFeatureAbility.addTagsOnCrete(creatingOrder);
        /**
         * 新增扩展数据 写入 tc_order_extend 表
         * 基础实现 DefaultOrderFeatureExt
         */
        orderExtendsAbility.addExtendOnCrete(creatingOrder);
    }
}
