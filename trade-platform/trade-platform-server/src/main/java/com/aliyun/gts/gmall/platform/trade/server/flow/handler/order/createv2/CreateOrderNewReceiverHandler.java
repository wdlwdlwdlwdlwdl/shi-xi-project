package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderReceiverAbility;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 创建订单 step 4
 *    每个商品的收货地址检查
 *    收货地址必须存在且可以用
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewReceiverHandler extends AdapterHandler<TOrderCreate> {

    // 对象转换
    @Autowired
    private TcOrderConverter tcOrderConverter;

    // 订单接收
    @Autowired
    private OrderReceiverAbility orderReceiverAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        // 入参
        CreateOrderRpcReq createOrderRpcReq = inbound.getReq();
        // 订单
        CreatingOrder creatingOrder = inbound.getDomain();
        Map<Long, TradeBizResult<ReceiveAddr>> receiveAddrMap = new HashMap<>();
        //不存在主单信息
        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
            if(CollectionUtils.isEmpty(mainOrder.getSubOrders())) {
                inbound.setError(CartErrorCode.CART_ITEM_STATUS_INVALID);
                return;
            }
            // 遍历每一个子订单
            for(SubOrder subOrder : mainOrder.getSubOrders()) {
                // 收货点不可为空
                if(Objects.isNull(subOrder.getReceiver()) ||
                    Objects.isNull(subOrder.getReceiver().getReceiverId())) {
                    inbound.setError(OrderErrorCode.RECEIVER_NOT_EXISTS);
                    return;
                }
                /**
                 * 收货点扩展计算，存在且有效
                 * pf4j定开 切入点
                 * 基础实现 DefaultOrderReceiverExt
                 * 通用实现 CommonOrderBizCheckExt
                 * 普通订单 NormalOrderBizCheckExt
                 * 电子产品 EvoucherOrderBizCheckExt
                 * 预算商品 PresaleOrderBizCheckExt
                 */
                // 如果没有收货地址 则跳过 存在收货地址则返回
                TradeBizResult<ReceiveAddr> result =
                    Objects.isNull(receiveAddrMap.get(subOrder.getReceiver().getReceiverId())) ?
                    orderReceiverAbility.checkOnConfirmOrder(createOrderRpcReq.getCustId(), subOrder.getReceiver(), creatingOrder) :
                    receiveAddrMap.get(subOrder.getReceiver().getReceiverId());
                if (result.isSuccess()) {
                    receiveAddrMap.put(subOrder.getReceiver().getReceiverId(), result);
                    subOrder.setReceiver(result.getData());
                    mainOrder.setReceiver(result.getData());
                } else {
                    inbound.setError(result.getFail());
                }
            }
        }
    }
}
