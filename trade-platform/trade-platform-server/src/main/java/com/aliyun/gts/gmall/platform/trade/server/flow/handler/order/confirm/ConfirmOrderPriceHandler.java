package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayMode;
import com.aliyun.gts.gmall.platform.trade.core.config.TradeLimitConfiguration;
import com.aliyun.gts.gmall.platform.trade.core.convertor.PromotionConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderPriceService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfirmOrderPriceHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private OrderPriceService orderPriceService;

    @Autowired
    private PromotionConverter promotionConverter;

    @Autowired
    private TradeLimitConfiguration tradeLimitConfiguration;

    @Override
    public void handle(TOrderConfirm inbound) {
        CreatingOrder order = inbound.getDomain();
        orderPriceService.calcOrderPrice(order);

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
