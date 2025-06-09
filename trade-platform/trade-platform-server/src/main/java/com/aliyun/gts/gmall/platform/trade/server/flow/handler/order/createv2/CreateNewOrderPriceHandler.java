package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderSeparateFeeAbility;
import com.aliyun.gts.gmall.platform.trade.core.config.TradeLimitConfiguration;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderPriceService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单创建 step 8
 *    订单价格计算处理
 *    总价 优惠价等信息计算
 *    判断是够能买不能超过限售上线
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateNewOrderPriceHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderPriceService orderPriceService;

    @Autowired
    private OrderSeparateFeeAbility orderSeparateFeeAbility;

    @Autowired
    private TradeLimitConfiguration tradeLimitConfiguration;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder creatingOrder = inbound.getDomain();
        /**
         * 重新计算价格
         */
        orderPriceService.recalcOrderPrice(creatingOrder);
        // 记录分账规则
//        for (MainOrder mainOrder : creatingOrder.getMainOrders()) {
//            orderSeparateFeeAbility.storeSeparateRule(mainOrder);
//        }
        // 获取支付方式
        PayModeCode payModeCode =  PayModeCode.codeOf(creatingOrder.getPayMode());
        //判断是否超过了最大支付限制
        // installment
        if (PayModeCode.isInstallment(payModeCode)) {
            if (creatingOrder.getOrderPrice().getOrderRealAmt() > tradeLimitConfiguration.getInstallmentLimit()) {
                inbound.setError(OrderErrorCode.INSTALLMENT_OVER_LIMIT);
            }
        }
        // loan
        if (PayModeCode.isLoan(payModeCode)) {
            if (creatingOrder.getOrderPrice().getOrderRealAmt() > tradeLimitConfiguration.getLoanLimit()) {
                inbound.setError(OrderErrorCode.LOAN_OVER_LIMIT);
            }
        }
    }
}
