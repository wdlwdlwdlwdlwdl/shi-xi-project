package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayMode;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderSeparateFeeAbility;
import com.aliyun.gts.gmall.platform.trade.core.config.TradeLimitConfiguration;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderPriceService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderPriceHandler extends AdapterHandler<TOrderCreate> {

    // 金额限制
    @Autowired
    private TradeLimitConfiguration tradeLimitConfiguration;

    @Autowired
    private OrderPriceService orderPriceService;

    @Autowired
    private OrderSeparateFeeAbility orderSeparateFeeAbility;



    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();

        // 计算价格
        orderPriceService.recalcOrderPrice(order);

        // 记录分账规则
        for (MainOrder mainOrder : order.getMainOrders()) {
            orderSeparateFeeAbility.storeSeparateRule(mainOrder);
        }

        //判断是否超过了最大支付限制
        if (order.getPayMode().startsWith(PayMode.LOAN)) {
            if (order.getOrderPrice().getOrderRealAmt()> tradeLimitConfiguration.getLoanLimit()) {
                inbound.setError(OrderErrorCode.LOAN_OVER_LIMIT);
            }
        }
        else if (order.getPayMode().startsWith(PayMode.INSTALLMENT)) {
            if (order.getOrderPrice().getOrderRealAmt() > tradeLimitConfiguration.getInstallmentLimit()) {
                inbound.setError(OrderErrorCode.INSTALLMENT_OVER_LIMIT);
            }
        }
    }
}
