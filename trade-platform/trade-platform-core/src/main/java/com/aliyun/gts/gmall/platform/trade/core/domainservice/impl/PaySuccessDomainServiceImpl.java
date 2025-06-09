package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OversaleMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.InventoryReduceType;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PaySuccessDomainService;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PaySuccessDomainServiceImpl implements PaySuccessDomainService {

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderInventoryAbility orderInventoryAbility;

    @Autowired
    private MessageSendManager messageSendManager;

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private PayQueryService payQueryService;

    @Value("${trade.order.oversale.topic:GMALL_TRADE_ORDER_OVERSALE}")
    private String overSaleTopic;


    @Override
    public TradeBizResult paySuccessExecute(PaySuccessMessage message) {
        log.info("receive PaySuccessMessage = " + JSONObject.toJSONString(message));
        MainOrder mainOrder = orderQueryAbility.getMainOrder(
            Long.parseLong(message.getPrimaryOrderId()),
            OrderQueryOption.builder().build()
        );
        if (!isWaitPay(mainOrder, message)) {
            return TradeBizResult.ok();
        }
        OrderPay orderPay = payQueryService.getOrderPay(message);
        // 付款减库存
        if (mainOrder.getInventoryReduceType().intValue() == InventoryReduceType.REDUCE_ON_PAY.getCode().intValue()) {
            TradeBizResult reduceInvResult = reduceInventory(mainOrder);
            if (reduceInvResult.isFailed()) {
                return reduceInvResult;
            }
        }
        // 更新订单状态
        orderStatusService.onPaySuccess(mainOrder, orderPay);
        return TradeBizResult.ok();
    }

    private boolean isWaitPay(MainOrder mainOrder, PaySuccessMessage message) {
        // 普通订单
        if (!StepOrderUtils.isMultiStep(mainOrder)) {
            return OrderStatusEnum.ORDER_WAIT_PAY.getCode().equals(mainOrder.getPrimaryOrderStatus());
        }
        // 多阶段订单
        StepOrder stepOrder = mainOrder.getCurrentStepOrder();
        return Objects.equals(stepOrder.getStepNo(), message.getStepNo())
                && StepOrderStatusEnum.STEP_WAIT_PAY.getCode().equals(stepOrder.getStatus())
                && (OrderStatusEnum.ORDER_WAIT_PAY.getCode().equals(mainOrder.getPrimaryOrderStatus())
                || OrderStatusEnum.STEP_ORDER_DOING.getCode().equals(mainOrder.getPrimaryOrderStatus())
                || OrderStatusEnum.PARTIALLY_PAID.getCode().equals(mainOrder.getPrimaryOrderStatus()));
    }

    private TradeBizResult reduceInventory(MainOrder mainOrder) {
        // 多阶段后续阶段不处理
        if (StepOrderUtils.isMultiStep(mainOrder) && !StepOrderUtils.isFirstStep(mainOrder)) {
            return TradeBizResult.ok();
        }
        try {
            List<MainOrder> list = new ArrayList<>(List.of(mainOrder));
            boolean success = orderInventoryAbility.lockInventory(list);
            if (success) {
                success = orderInventoryAbility.reduceInventory(list);
            }
            if (!success) { // 超卖了
                sendOverSale(mainOrder, false);
            }
        } catch (Exception e) {
            log.error("inventoryRepository.reduceInventory occurred exceptions!", e);
            sendOverSale(mainOrder, true);
        }
        return TradeBizResult.ok();
    }

    private void sendOverSale(MainOrder mainOrder, boolean isException) {
        OversaleMessageDTO msg = new OversaleMessageDTO(mainOrder.getPrimaryOrderId(), isException);
        messageSendManager.sendMessage(msg, overSaleTopic, "OVER_SALE");
    }

}
