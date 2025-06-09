package com.aliyun.gts.gmall.platform.trade.domain.util;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.RefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StepOrderUtils {

    public static boolean isMultiStep(MainOrder mainOrder) {
        return OrderTypeEnum.MULTI_STEP_ORDER.getCode().equals(mainOrder.getOrderType());
    }

    public static boolean isMultiStep(MainReversal mainReversal) {
        MainOrder mainOrder = mainReversal.getMainOrder();
        if (mainOrder != null && mainOrder.getOrderType() != null) {
            return isMultiStep(mainOrder);
        }
        Map<Integer, RefundFee> stepFee = mainReversal.reversalFeatures().getStepRefundFee();
        return MapUtils.isNotEmpty(stepFee);
    }

    public static List<Integer> getSteps(MainReversal reversal) {
        Map<Integer, RefundFee> map = reversal.reversalFeatures().getStepRefundFee();
        if (map == null) {
            return new ArrayList<>();
        }
        List<Integer> steps = new ArrayList<>();
        for (Map.Entry<Integer, RefundFee> en : map.entrySet()) {
            RefundFee fee = en.getValue();
            if (!isAllZero(fee)) {
                steps.add(en.getKey());
            }
        }
        return steps;
    }

    private static boolean isAllZero(RefundFee fee) {
        return NumUtils.getNullZero(fee.getCancelPointAmt()) == 0L
                && NumUtils.getNullZero(fee.getCancelPointCount()) == 0L
                && NumUtils.getNullZero(fee.getCancelRealAmt()) == 0L;
    }

    public static boolean isFirstStep(MainOrder mainOrder) {
        if (CollectionUtils.isEmpty(mainOrder.getStepOrders())
                || mainOrder.getOrderAttr().getCurrentStepNo() == null
                || !isMultiStep(mainOrder)) {
            return false;
        }
        Integer step = mainOrder.getOrderAttr().getCurrentStepNo();
        Integer firstStepNo = mainOrder.getStepOrders().get(0).getStepNo();
        return Objects.equals(step, firstStepNo);
    }

    public static StepOrder getStepOrder(MainOrder mainOrder, Integer stepNo) {
        if (stepNo == null
                || mainOrder.getStepOrders() == null
                || !isMultiStep(mainOrder)) {
            return null;
        }
        return mainOrder.getStepOrders().stream()
                .filter(step -> Objects.equals(step.getStepNo(), stepNo))
                .findFirst().orElse(null);
    }

    public static boolean isPaid(StepOrder stepOrder) {
        StepOrderStatusEnum stepStatus = StepOrderStatusEnum.codeOf(stepOrder.getStatus());
        return stepStatus != StepOrderStatusEnum.STEP_WAIT_START
                && stepStatus != StepOrderStatusEnum.STEP_WAIT_PAY;
    }
}
