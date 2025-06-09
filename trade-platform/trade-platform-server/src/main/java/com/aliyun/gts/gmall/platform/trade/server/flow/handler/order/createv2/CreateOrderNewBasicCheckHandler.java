package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户下单 step1
 *    入参基本信息check
 * @anthor shifeng
 * 2024-12-13 11:43:55
 */
@Slf4j
@Component
public class CreateOrderNewBasicCheckHandler extends AdapterHandler<TOrderCreate> {

    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq createOrderRpcReq = inbound.getReq();
        // 获取支付方式
        PayModeCode payModeCode = PayModeCode.codeOf(createOrderRpcReq.getPayMode());
        if (payModeCode == null) {
            //不包含，报错
            log.error("payModeCode is null");
            inbound.setError(OrderErrorCode.PAY_MODEL_ILLEGAL);
            return;
        }
        //判断新选的orderItems 小于等于 token存储的，因为只允许修改confirm过后的
        List<CreateItemInfo> orderItems = createOrderRpcReq.getOrderItems();
        if (CollectionUtils.isEmpty(orderItems)) {
            //报错
            inbound.setError(OrderErrorCode.ORDER_ITEM_NULL);
            return;
        }
    }
}