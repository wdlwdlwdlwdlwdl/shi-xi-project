package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCheckAbility;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建订单 step 7
 *   订单业务校验，校验购买信息等
 *   校验下单限制信息（项目上不用）
 *   通过扩展点 多态实现
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewBizCheckHandler extends AdapterHandler<TOrderCreate> {

    // 下单校验check
    @Autowired
    private OrderBizCheckAbility orderBizCheckAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        /**
         * 下单订单确认check 业务校验
         * 扩展能力入口
         * 基础实现  DefaultOrderBizCheckExt
         * 不同商品使用不同的扩展点
         *     通用（除了赠品等特殊场景） CommonOrderBizCheckExt
         *     电子商品处理类 EvoucherOrderBizCheckExt
         *     普通订单处理类 NormalOrderBizCheckExt
         *     预售商品处理类 PresaleOrderBizCheckExt
         *
         */
        TradeBizResult tradeBizResult = orderBizCheckAbility.checkOnCreateOrder(inbound.getDomain());
        if (Boolean.FALSE.equals(tradeBizResult.isSuccess())) {
            inbound.setError(tradeBizResult.getFail());
        }
    }
}
