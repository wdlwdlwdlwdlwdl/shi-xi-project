package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizRollbackAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderPointService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateOrderErrorRollbackHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderPointService orderPointService;

    @Autowired
    private OrderInventoryAbility orderInventoryAbility;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private OrderBizRollbackAbility orderBizRollbackAbility;

    @Override
    public void handle(TOrderCreate inbound) {
    }

    @Override
    public void handleError(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();

        // 释放锁定的积分
        if (inbound.getExtra("pointLock") != null) {
            try {
                orderPointService.unlockPoint(order);
            } catch (Exception e) {
                log.error("orderPointService.unlockPoint", e);
            }
        }

        // 释放预占的库存
        /*if (inbound.getExtra("inventoryLock") != null) {
            try {
                orderInventoryAbility.unlockInventory(inbound.getDomain().getMainOrders());
            } catch (Exception e) {
                log.error("orderInventoryService.unlockInventory", e);
            }
        }*/

        // 营销资产释放
        if (inbound.getExtra("deductUserAssets") != null) {
            try {
                promotionRepository.rollbackUserAssets(order);
            } catch (Exception e) {
                log.error("promotionRepository.rollbackUserAssets", e);
            }
        }

        // 扩展
        try {
            orderBizRollbackAbility.rollbackBizResource(order);
        } catch (Exception e) {
            log.error("orderBizRollbackAbility.rollbackBizResource", e);
        }
    }
}
