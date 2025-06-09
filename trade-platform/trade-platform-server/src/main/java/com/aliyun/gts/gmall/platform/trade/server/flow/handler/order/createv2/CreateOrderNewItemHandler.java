package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.core.ability.OrderFeatureAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import com.aliyun.gts.gmall.platform.trade.server.utils.BizCodeHandlerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建订单 step 5
 *    商品信息扩展点扩展处理
 *    商品信息查询在step2 已完成
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewItemHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;

    @Autowired
    private OrderFeatureAbility orderFeatureAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder creatingOrder = inbound.getDomain();
        /**
         * 商品业务身份 --- 商品业务扩展点
         * 通过PF4J扩展能力扩展定开
         * 基类 DefaultOrderBizCodeExt
         * 扩展类 MatchAllOrderBizCodeExt 实现
         */
        BizCodeHandlerUtils.fillBizCode(inbound, orderBizCodeAbility::getBizCodesFromItem);

        /**
         * 商品扩展数据扩展点
         * 通过PF4J扩展能力扩展定开
         * 基类 DefaultOrderFeatureExt
         * 扩展类 MatchAllOrderFeatureExt
         */
        orderFeatureAbility.addItemFeature(creatingOrder);
    }
}
