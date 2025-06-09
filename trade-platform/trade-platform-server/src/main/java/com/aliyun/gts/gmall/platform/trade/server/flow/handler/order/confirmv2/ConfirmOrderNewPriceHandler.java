package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OverPriceLimitEnum;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.core.config.TradeLimitConfiguration;
import com.aliyun.gts.gmall.platform.trade.core.convertor.PromotionConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderPriceService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单确认 step7
 *    下单交易 所有的交易金额计算
 */
@Component
public class ConfirmOrderNewPriceHandler extends AdapterHandler<TOrderConfirm> {

    // 金额限制
    @Autowired
    private TradeLimitConfiguration tradeLimitConfiguration;

    @Autowired
    private OrderPriceService orderPriceService;

    @Autowired
    private PromotionConverter promotionConverter;
    
    @Override
    public void handle(TOrderConfirm inbound) {
        CreatingOrder creatingOrder = inbound.getDomain();
        /**
         * 计算订单金额方法
         * 通过扩展点计算每个交易场景
         * 基础实现类 DefaultPriceCalcExt
         * 扩展实现类 CommonPriceCalcExt
         */
        orderPriceService.calcOrderPriceNew(creatingOrder);
        // 获取支付方式
        PayModeCode payModeCode = PayModeCode.codeOf(creatingOrder.getPayMode());
        //判断是否超过了最大支付限制 超过了 不可以购买
        // installment
        if (PayModeCode.isInstallment(payModeCode)) {
            if (creatingOrder.getOrderPrice().getOrderRealAmt() > tradeLimitConfiguration.getInstallmentLimit()) {
                creatingOrder.setConfirmSuccess(Boolean.FALSE);
                creatingOrder.setOverPriceLimit(OverPriceLimitEnum.INSTALLMENT_OVER_LIMIT.getCode());
                creatingOrder.setErrMsg(I18NMessageUtils.getMessage("installment.over.limit"));
            }
        }
        // loan
        if (PayModeCode.isLoan(payModeCode)) {
            if (creatingOrder.getOrderPrice().getOrderRealAmt() > tradeLimitConfiguration.getLoanLimit()) {
                creatingOrder.setConfirmSuccess(Boolean.FALSE);
                creatingOrder.setOverPriceLimit(OverPriceLimitEnum.CREDIT_OVER_LIMIT.getCode());
                creatingOrder.setErrMsg(I18NMessageUtils.getMessage("loan.over.limit"));
            }
        }
    }
}
