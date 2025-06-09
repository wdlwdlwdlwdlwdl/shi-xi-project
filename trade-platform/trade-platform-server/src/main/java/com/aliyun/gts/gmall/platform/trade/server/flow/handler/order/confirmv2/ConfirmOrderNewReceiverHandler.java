package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.DeliveryTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderReceiverAbility;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemDelivery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import com.aliyun.gts.gmall.platform.user.api.enums.DeliveryMethodEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.platform.user.api.enums.DeliveryMethodEnum.HOME_DELIVERY;

/**
 * 订单确认 step3
 *    收货地址check&赋值
 *    check订单时候 可能没有明确的收货地址
 *    不同于标品，每个商品都有一个收货地址， 逐个计算
 */
@Component
public class ConfirmOrderNewReceiverHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private TcOrderConverter tcOrderConverter;

    // 收货地址扩展点
    @Autowired
    private OrderReceiverAbility orderReceiverAbility;

    @Override
    public void handle(TOrderConfirm inbound) {
        CreatingOrder creatingOrder = inbound.getDomain();
        ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq = inbound.getReq();
        // 收货地址map
        Map<Long, TradeBizResult<ReceiveAddr>> receiveAddrMap = new HashMap<>();
        ReceiveAddr defaultReceive = null;
        // 查询入参的收货地址 是否存在默认的一个地址
        if (Objects.nonNull(confirmOrderInfoRpcReq.getReceiverId())) {
            ReceiveAddr receiveAddr = new ReceiveAddr();
            receiveAddr.setReceiverId(confirmOrderInfoRpcReq.getReceiverId());
            TradeBizResult<ReceiveAddr> defaultReceiveResult = orderReceiverAbility.checkOnConfirmOrder(confirmOrderInfoRpcReq.getCustId(), receiveAddr, creatingOrder);
            if (Objects.nonNull(defaultReceiveResult) &&
                Boolean.TRUE.equals(defaultReceiveResult.isSuccess()) &&
                Objects.nonNull(defaultReceiveResult.getData())) {
                defaultReceive = defaultReceiveResult.getData();
                receiveAddrMap.put(confirmOrderInfoRpcReq.getReceiverId(), defaultReceiveResult);
            }
        }
        // 遍历每一个商品
        for (MainOrder mainOrder : creatingOrder.getMainOrders()){
            // 参数判断
            if (Objects.isNull(mainOrder) ||
                CollectionUtils.isEmpty(mainOrder.getSubOrders())) {
                //不可以下单
                creatingOrder.setConfirmSuccess(Boolean.FALSE);
                continue;
            }
            //  遍历每一个子单 (每个商品查询一次)
            for(SubOrder subOrder : mainOrder.getSubOrders()){
                // 入参没有选择地址 设置默认地址
                if (Objects.isNull(subOrder.getReceiver()) ||
                    Objects.isNull(subOrder.getReceiver().getReceiverId())) {
                    // 设置默认地址
                    Boolean setSuccess = settingDefaultRecever(mainOrder, subOrder, defaultReceive);
                    if(!setSuccess) {
                        creatingOrder.setConfirmSuccess(setSuccess);
                    }
                    continue;
                }
                // 如果没有收货地址 则跳过 存在收货地址则返回
                TradeBizResult<ReceiveAddr> result =
                    Objects.isNull(receiveAddrMap.get(subOrder.getReceiver().getReceiverId())) ?
                    orderReceiverAbility.checkOnConfirmOrder(confirmOrderInfoRpcReq.getCustId(), subOrder.getReceiver(), creatingOrder) :
                    receiveAddrMap.get(subOrder.getReceiver().getReceiverId());
                if (Boolean.TRUE.equals(result.isSuccess()) && Objects.nonNull(result.getData())) {
                    // 缓存查询结果
                    receiveAddrMap.put(subOrder.getReceiver().getReceiverId(), result);
                    // 设置收货地址
                    subOrder.setReceiver(result.getData());
                    mainOrder.setReceiver(result.getData());
                    // 设置物流时效
                    subOrder.setItemDelivery(calcDeliveryTime(subOrder, result.getData().getDeliveryMethod()));
                } else  {
                    creatingOrder.setConfirmSuccess(Boolean.FALSE);
                }
            }
        }
    }

    /**
     * 计算是否可以使用默认值
     * @param mainOrder
     * @param subOrder
     * @param defaultReceive
     * 2025-2-25 11:17:02
     */
    private Boolean settingDefaultRecever(MainOrder mainOrder, SubOrder subOrder, ReceiveAddr defaultReceive) {
        // 为空 跳过
        if (CollectionUtils.isEmpty(subOrder.getItemDelivery()) ||
            Objects.isNull(defaultReceive)) {
            return Boolean.FALSE;
        }
        // 如果支持送货上门，且有默认送货上门的地址，则使用默认地址 直接填充
        List<ItemDelivery> defaultItemDelivery = subOrder.getItemDelivery()
            .stream()
            .filter(itemDelivery ->
                DeliveryTypeEnum.SELF_SERVICE.getScript().equals(itemDelivery.getDeliveryTypeName()) ||
                DeliveryTypeEnum.HM_SERVICE.getScript().equals(itemDelivery.getDeliveryTypeName()))
            .collect(Collectors.toList());
        // 支持送货上门
        if (CollectionUtils.isNotEmpty(defaultItemDelivery)) {
            subOrder.setItemDelivery(defaultItemDelivery);
            subOrder.setReceiver(defaultReceive);
            mainOrder.setReceiver(defaultReceive);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 计算物流时效 只留下收货地址的物流时效
     * @param subOrder
     * @param deliveryMethod
     */
    private List<ItemDelivery> calcDeliveryTime(SubOrder subOrder, String deliveryMethod) {
        // 为空 跳过
        if (CollectionUtils.isEmpty(subOrder.getItemDelivery()) ||
            StringUtils.isEmpty(deliveryMethod)) {
            return subOrder.getItemDelivery();
        }
        // 送货上门
        if (DeliveryMethodEnum.HOME_DELIVERY.equals(deliveryMethod)) {
           return subOrder.getItemDelivery()
               .stream()
               .filter(itemDelivery ->
                    DeliveryTypeEnum.SELF_SERVICE.getScript().equals(itemDelivery.getDeliveryTypeName()) ||
                    DeliveryTypeEnum.HM_SERVICE.getScript().equals(itemDelivery.getDeliveryTypeName()))
               .collect(Collectors.toList());
        }
        // PVZ
        if (DeliveryMethodEnum.PVZ.equals(deliveryMethod)) {
            return subOrder.getItemDelivery()
                .stream()
                .filter(itemDelivery -> DeliveryTypeEnum.PVZ.getScript().equals(itemDelivery.getDeliveryTypeName()))
                .collect(Collectors.toList());
        }
        // POSAMAT
        if (DeliveryMethodEnum.POSAMAT.equals(deliveryMethod)) {
            return subOrder.getItemDelivery().stream()
                .filter(itemDelivery -> DeliveryTypeEnum.POSTAMAT.getScript().equals(itemDelivery.getDeliveryTypeName()))
                .collect(Collectors.toList());
        }
        // PICK_UP
        if (DeliveryMethodEnum.PICK_UP.equals(deliveryMethod)) {
            return subOrder.getItemDelivery()
                .stream()
                .filter(itemDelivery -> DeliveryTypeEnum.WAREHOURSE_PICK_UP.getScript().equals(itemDelivery.getDeliveryTypeName()))
                .collect(Collectors.toList());
        }
        return subOrder.getItemDelivery();
    }
}
