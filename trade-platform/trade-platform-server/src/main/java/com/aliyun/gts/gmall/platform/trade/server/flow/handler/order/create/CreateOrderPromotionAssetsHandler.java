package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单创建 step 12
 *    核销优惠券计算
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderPromotionAssetsHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder creatingOrder = inbound.getDomain();
        inbound.putExtra("deductUserAssets", true);
        // 核销优惠券计算
        promotionRepository.deductUserAssets(creatingOrder);
    }
}
