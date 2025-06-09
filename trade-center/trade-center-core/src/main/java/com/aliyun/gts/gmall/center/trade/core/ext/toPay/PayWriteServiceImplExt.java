package com.aliyun.gts.gmall.center.trade.core.ext.toPay;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.impl.PayWriteServiceImpl;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class PayWriteServiceImplExt extends PayWriteServiceImpl {

    @Override
    public void checkToPay(MainOrder mainOrder) {
        if (mainOrder == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        boolean isWaitPay = OrderStatusEnum.WAITING_FOR_PAYMENT.getCode().equals(mainOrder.getPrimaryOrderStatus());
        boolean isStepWaitPay =
            (OrderStatusEnum.STEP_ORDER_DOING.getCode().equals(mainOrder.getPrimaryOrderStatus()) ||
            OrderStatusEnum.PARTIALLY_PAID.getCode().equals(mainOrder.getPrimaryOrderStatus())) &&
                StepOrderStatusEnum.STEP_WAIT_PAY.getCode().equals(mainOrder.getCurrentStepOrder().getStatus()
            );
        if (!isWaitPay && !isStepWaitPay) {
            throw new GmallException(OrderErrorCode.ORDER_STATUS_ILLEGAL);
        }
    }
}
