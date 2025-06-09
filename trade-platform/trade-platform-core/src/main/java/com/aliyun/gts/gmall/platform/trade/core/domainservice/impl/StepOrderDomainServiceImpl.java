package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmStepExtendDTO;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.StepOrderCreateAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.StepOrderProcessAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.StepOrderDomainService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StepOrderDomainServiceImpl implements StepOrderDomainService {

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private StepOrderCreateAbility stepOrderCreateAbility;

    @Autowired
    private StepOrderProcessAbility stepOrderProcessAbility;

    @Override
    public void fillStepInfoOnCreate(MainOrder mainOrder) {
        /**
         * 读取营销配置表的富多步骤配置信息
         * 扩展点计算
         */
        stepOrderCreateAbility.fillStepTemplate(mainOrder);
        /**
         * 填充分部处理逻辑
         * 扩展点计算
         */
        stepOrderCreateAbility.fillStepOrders(mainOrder);
    }

    /**
     * 分步订单金额计算
     * @param creatingOrder
     * @return
     */
    @Override
    public ConfirmStepExtendDTO calcStepExtend(CreatingOrder creatingOrder) {
        /** 基础处理类 DefaultStepOrderCreateExt */
        return stepOrderCreateAbility.calcStepExtend(creatingOrder);
    }

    /**
     * 创建订单时候 分步校验
     * @param mainOrder
     * 2024-12-13 14:19:27
     */
    @Override
    public void checkStepInfoOnCreate(MainOrder mainOrder) {
        // 获取旧的多步骤计算模板配置
        String oldName = mainOrder.getStepTemplate().getTemplateName();
        Map<Integer, StepOrderPrice> oldPriceMap = mainOrder
            .getStepOrders()
            .stream()
            .collect(Collectors.toMap(StepOrder::getStepNo, StepOrder::getPrice));

        // 重新计算
        fillStepInfoOnCreate(mainOrder);

        // 获取新的多步骤计算模板配置
        String newName = mainOrder.getStepTemplate().getTemplateName();
        Map<Integer, StepOrderPrice> newPriceMap = mainOrder
            .getStepOrders()
            .stream()
            .collect(Collectors.toMap(StepOrder::getStepNo, StepOrder::getPrice));

        /**
         * 前后校验需要相同
         * 校验结果 否则报错
         */
        if (!StringUtils.equals(oldName, newName)) {
            throw new GmallException(OrderErrorCode.MULTI_STEP_DISCORD);
        }
        if (!oldPriceMap.equals(newPriceMap)) {
            throw new GmallException(OrderErrorCode.MULTI_STEP_DISCORD);
        }
    }

    @Override
    public void onPaySuccess(MainOrder mainOrder, OrderPay orderPay) {
        stepOrderProcessAbility.onPaySuccess(mainOrder, orderPay);
    }

    @Override
    public void handleStepOrderBySeller(StepOrderHandleRpcReq req) {
        OrderQueryOption opt = OrderQueryOption.builder().build();
        MainOrder mainOrder = orderQueryAbility.getMainOrder(req.getPrimaryOrderId(), opt);
        if (mainOrder == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        stepOrderProcessAbility.onSellerHandle(mainOrder, req);
    }

    @Override
    public void confirmStepOrderByCustomer(StepOrderHandleRpcReq req) {
        OrderQueryOption opt = OrderQueryOption.builder().build();
        MainOrder mainOrder = orderQueryAbility.getMainOrder(req.getPrimaryOrderId(), opt);
        if (mainOrder == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        stepOrderProcessAbility.onCustomerConfirm(mainOrder, req);
    }
}
