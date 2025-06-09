package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 *
 */
@Component
public class CreateOrderNewPromotionAssetsHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();
        inbound.putExtra("deductUserAssets", true);
        promotionRepository.deductUserAssets(order);
    }
}
