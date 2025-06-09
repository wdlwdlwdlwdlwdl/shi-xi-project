package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizRollbackAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单创建 step 14
 *     下单异常场景计算处理
 *     如果下单失败，则回滚占用的资源
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
@Slf4j
public class CreateOrderNewErrorRollbackHandler extends AdapterHandler<TOrderCreate> {

//    @Autowired
//    private OrderPointService orderPointService;

//    @Autowired
//    private OrderInventoryAbility orderInventoryAbility;

    @Autowired
    private OrderBizRollbackAbility orderBizRollbackAbility;

    @Autowired
    private PromotionRepository promotionRepository;

    @Override
    public void handle(TOrderCreate inbound) {

    }

    @Override
    public void handleError(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();
        // 释放锁定的积分
        //if (inbound.getExtra("pointLock") != null) {
        //    try {
        //        orderPointService.unlockPoint(order);
        //    } catch (Exception e) {
        //        log.error("orderPointService.unlockPoint", e);
        //    }
        //}
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
