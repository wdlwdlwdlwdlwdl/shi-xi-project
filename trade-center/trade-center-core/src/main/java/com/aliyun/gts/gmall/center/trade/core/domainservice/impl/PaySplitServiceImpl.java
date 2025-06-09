package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.core.domainservice.PaySplitService;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaySplitServiceImpl implements PaySplitService {

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Override
    public TradeBizResult paySplitAfterTradeSuccess(Long primaryOrderId) {
        //查询主子订单信息
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId, OrderQueryOption.builder().build());
        if (mainOrder == null) {
            log.error("OrderSuccessConsumer.process query mainOrder is null; primaryOrderId = " + primaryOrderId);
            return TradeBizResult.fail(OrderErrorCode.ORDER_NOT_EXISTS.getCode(), OrderErrorCode.ORDER_NOT_EXISTS.getMessage());
        }

        if (StepOrderUtils.isMultiStep(mainOrder)) {
            // 处理每个阶段的支付
            TradeBizResult r = TradeBizResult.ok();
            for (StepOrder stepOrder : mainOrder.getStepOrders()) {
                if (stepOrder.getPrice().getConfirmPrice() != null) {
                    r = paySplitAfterTradeSuccess(mainOrder, stepOrder.getStepNo());
                    if (!r.isSuccess()) {
                        return r;
                    }
                }
            }
            return r;
        } else {
            return paySplitAfterTradeSuccess(mainOrder, null);
        }
    }

    private TradeBizResult paySplitAfterTradeSuccess(MainOrder mainOrder, Integer stepNo) {
        orderPayRepository.settle(mainOrder, stepNo);
        return TradeBizResult.ok();
    }
}
