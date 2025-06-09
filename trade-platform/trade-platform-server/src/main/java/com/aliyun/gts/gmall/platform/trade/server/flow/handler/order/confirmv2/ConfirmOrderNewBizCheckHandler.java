package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCheckAbility;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单确认 step6
 *   订单业务校验，校验购买信息等
 *   校验下单限制信息（项目上不用）
 *   通过扩展点 多态实现！
 */
@Component
public class ConfirmOrderNewBizCheckHandler extends AdapterHandler<TOrderConfirm> {

    // 下单校验check
    @Autowired
    private OrderBizCheckAbility orderBizCheckAbility;

    @Override
    public void handle(TOrderConfirm inbound) {
        CreatingOrder creatingOrder = inbound.getDomain();
        /**
         * 下单订单确认check 业务校验
         * 扩展能力入口
         * 基础实现  DefaultOrderBizCheckExt
         * 不通商品使用不同的扩展点
         *     通用（除了赠品等特殊场景） CommonOrderBizCheckExt
         *     电子商品处理类 EvoucherOrderBizCheckExt
         *     普通订单处理类 NormalOrderBizCheckExt
         *     预售商品处理类 PresaleOrderBizCheckExt
         */
        TradeBizResult result = orderBizCheckAbility.checkOnConfirmOrder(inbound.getDomain());
        if (Boolean.FALSE.equals(result.isSuccess())) {
            inbound.setError(result.getFail());
            creatingOrder.setConfirmSuccess(Boolean.FALSE);
        }
    }
}
