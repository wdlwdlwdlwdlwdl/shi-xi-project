package com.aliyun.gts.gmall.platform.trade.server.utils;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmStepOrderInfo;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepTemplate;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCodeExt.OrderBizCode;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCodeExt.OrderBizCodeReq;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

public class BizCodeHandlerUtils {

    public static void fillBizCode(TOrderConfirm in, Function<OrderBizCodeReq, TradeBizResult<OrderBizCode>> fn) {
        OrderBizCodeReq req = getRequestBizCode(in);
        TradeBizResult<OrderBizCode> result = fn.apply(req);
        fillBizResult(result, in);

    }

    public static void fillBizCode(TOrderCreate in, Function<OrderBizCodeReq, TradeBizResult<OrderBizCode>> fn) {
        OrderBizCodeReq req = getRequestBizCode(in);
        TradeBizResult<OrderBizCode> result = fn.apply(req);
        fillBizResult(result, in);
    }

    private static OrderBizCodeReq getRequestBizCode(TOrderConfirm inbound) {
        // 请求级
        List<String> bizCodes = inbound.getReq().getBizCode();
        Integer orderType = isMultiStep(inbound) ? OrderTypeEnum.MULTI_STEP_ORDER.getCode() : null;
        // 订单上的
        if (CollectionUtils.isNotEmpty(inbound.getDomain().getMainOrders())) {
            for (MainOrder mainOrder : inbound.getDomain().getMainOrders()) {
                if (CollectionUtils.isNotEmpty(mainOrder.getBizCodes())) {
                    bizCodes = mainOrder.getBizCodes();
                    orderType = mainOrder.getOrderType();
                }
            }
        }
        return OrderBizCodeReq.builder()
                .requestBizCodes(bizCodes)
                .requestOrderType(orderType)
                .order(inbound.getDomain())
                .build();
    }

    private static OrderBizCodeReq getRequestBizCode(TOrderCreate inbound) {
        // 请求级
        List<String> bizCodes = null;
        Integer orderType = isMultiStep(inbound) ? OrderTypeEnum.MULTI_STEP_ORDER.getCode() : null;
        // 订单上的
        if (CollectionUtils.isNotEmpty(inbound.getDomain().getMainOrders())) {
            for (MainOrder mainOrder : inbound.getDomain().getMainOrders()) {
                if (CollectionUtils.isNotEmpty(mainOrder.getBizCodes())) {
                    bizCodes = mainOrder.getBizCodes();
                    orderType = mainOrder.getOrderType();
                }
            }
        }
        return OrderBizCodeReq.builder()
                .requestBizCodes(bizCodes)
                .requestOrderType(orderType)
                .order(inbound.getDomain())
                .build();
    }

    private static void fillBizResult(TradeBizResult<OrderBizCode> result,
                                      AbstractContextEntity<?, CreatingOrder, ?> inbound) {
        if (!result.isSuccess()) {
            inbound.setError(result.getFail());
            return;
        }
        CreatingOrder order = inbound.getDomain();
        OrderBizCode bizCode = result.getData();
        order.getMainOrders().stream().forEach(main -> {
            main.setBizCodes(bizCode.getBizCodes());
            main.setOrderType(bizCode.getOrderType());
        });
    }

    private static boolean isMultiStep(TOrderConfirm inbound) {
        ConfirmStepOrderInfo stepInfo = inbound.getReq().getConfirmStepOrderInfo();
        return stepInfo != null && StringUtils.isNotBlank(stepInfo.getStepTemplateName());
    }

    private static boolean isMultiStep(TOrderCreate inbound) {
        StepTemplate stepInfo = inbound.getDomain().getMainOrders().get(0).getStepTemplate();
        return stepInfo != null && StringUtils.isNotBlank(stepInfo.getTemplateName());
    }
}
