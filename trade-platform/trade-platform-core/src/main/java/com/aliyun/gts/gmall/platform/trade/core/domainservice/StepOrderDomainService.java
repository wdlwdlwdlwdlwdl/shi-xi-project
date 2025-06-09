package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmStepExtendDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;

public interface StepOrderDomainService {

    /**
     * 确认订单处理stepOrder
     */
    void fillStepInfoOnCreate(MainOrder mainOrder);

    /**
     * 计算扩展信息 (firstPay / remainPay)
     */
    ConfirmStepExtendDTO calcStepExtend(CreatingOrder c);

    /**
     * 下单处理stepOrder
     */
    void checkStepInfoOnCreate(MainOrder mainOrder);

    /**
     * 多阶段支付成功
     */
    void onPaySuccess(MainOrder mainOrder, OrderPay orderPay);

    /**
     * 多阶段卖家处理
     */
    void handleStepOrderBySeller(StepOrderHandleRpcReq req);

    /**
     * 多阶段用户确认
     */
    void confirmStepOrderByCustomer(StepOrderHandleRpcReq req);
}
