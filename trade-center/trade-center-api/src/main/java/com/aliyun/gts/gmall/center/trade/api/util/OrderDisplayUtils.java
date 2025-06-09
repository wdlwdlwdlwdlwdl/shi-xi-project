package com.aliyun.gts.gmall.center.trade.api.util;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtOrderStatus;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtOrderType;
import com.aliyun.gts.gmall.platform.item.common.enums.ItemPriceloanPeriodTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.StepOrderDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PayTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.BaseStepOperate;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepMeta;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class OrderDisplayUtils {

    public static String getOrderTypeName(Integer orderType) {
        OrderTypeEnum type = OrderTypeEnum.codeOf(orderType);
        if (type != null) {
            return type.getName();
        }
        ExtOrderType extType = ExtOrderType.codeOf(orderType);
        if (extType != null) {
            return extType.getName();
        }
        return String.valueOf(orderType);
    }

    public static String getLoanCycleName(Integer loanCycle) {
        ItemPriceloanPeriodTypeEnum loanPeriodType = ItemPriceloanPeriodTypeEnum.valueOf(loanCycle);
        if (loanPeriodType != null) {
            return loanPeriodType.getName();
        }
        return String.valueOf(loanCycle);
    }

    public static String getStatusName(MainOrderDTO mainOrder, Integer status) {
        OrderStatusEnum statusEnum = OrderStatusEnum.codeOf(status);
        if (statusEnum == null) {
            ExtOrderStatus ext = ExtOrderStatus.codeOf(status);
            if (ext == null) {
                return String.valueOf(status);
            }
            return ext.getName();
        }
        // 多阶段状态名称
        if (OrderTypeEnum.codeOf(mainOrder.getOrderType()) == OrderTypeEnum.MULTI_STEP_ORDER &&
            (statusEnum == OrderStatusEnum.ORDER_WAIT_PAY ||
            statusEnum == OrderStatusEnum.STEP_ORDER_DOING ||
            statusEnum == OrderStatusEnum.PARTIALLY_PAID)) {
            try {
                StepMeta stepMeta = mainOrder.getStepTemplate().getStepMeta(mainOrder.getCurrentStepNo());
                StepOrderDTO stepOrder = mainOrder.getCurrentStepOrder();
                BaseStepOperate currOp = stepMeta.getOperate(stepOrder.getStatus());
                if (StringUtils.isNotBlank(currOp.getStatusName())) {
                    // edit by shifeng 状态取配置 配置i18n可实现多语言
                    return I18NMessageUtils.getMessage(currOp.getStatusName());
                }
            } catch (Exception e) {
                // ignore
            }
        }
        // 电子凭证状态名称
        //if (ExtOrderType.codeOf(mainOrder.getOrderType()) == ExtOrderType.EVOUCHER) {
        //    if (statusEnum == OrderStatusEnum.ORDER_SENDED) {
        //        return I18NMessageUtils.getMessage("voucher.sent");  //# "已发送凭证"
        //    } else if (statusEnum == OrderStatusEnum.ORDER_CONFIRM) {
        //        return I18NMessageUtils.getMessage("voucher.redeemed");  //# "已核销凭证"
        //    }
        //}
        // i18n 取翻译后的
        return statusEnum.getInner();
    }

    public static StepOrderDTO getCurrentStepOrder(MainOrderDTO mainOrder) {
        if (OrderTypeEnum.codeOf(mainOrder.getOrderType()) != OrderTypeEnum.MULTI_STEP_ORDER) {
            return null;
        }
        if (mainOrder.getStepOrders() == null) {
            return null;
        }
        Integer currentStep = mainOrder.getCurrentStepNo();
        if (currentStep == null) {
            return null;
        }
        return mainOrder.getStepOrders().stream()
                .filter(step -> step.getStepNo().intValue() == currentStep.intValue())
                .findFirst().orElse(null);
    }

    public static String getPayName(Integer status) {
        PayTypeEnum payEnum = PayTypeEnum.codeOf(status);
        if (payEnum != null) {
            return payEnum.getInner();
        }
        return String.valueOf(status);
    }
}
