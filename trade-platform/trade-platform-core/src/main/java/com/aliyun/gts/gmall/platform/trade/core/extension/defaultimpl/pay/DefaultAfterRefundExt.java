package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.pay;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.AfterRefundExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultAfterRefundExt implements AfterRefundExt {

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private OrderInventoryAbility orderInventoryAbility;

    @Override
    public TradeBizResult afterRefund(MainReversal mainReversal) {
        //目前只有售中（状态为待发货）仅退款需要回补库存
        if (!needRollbackInventory(mainReversal)) {
            return TradeBizResult.ok();
        }

        try {
            log.info("售后单库存回补");
            orderInventoryAbility.rollbackInventoryAfterRefund(mainReversal);

        } catch (Exception e) {
            log.error("inventoryRepository.rollbackInventory occurred exceptions!", e);
            // 弱依赖 库存回补, 商品下架删除等
            //return TradeBizResult.fail(PayErrorCode.PAY_ROLLBACK_INVENTORY_ERROR.getCode(), PayErrorCode.PAY_ROLLBACK_INVENTORY_ERROR.getMessage());
        }

        return TradeBizResult.ok();
    }

    private static boolean needRollbackInventory(MainReversal mainReversal) {
        log.info("售后单详情：{}", mainReversal.toString());
        return (ReversalTypeEnum.REFUND_ONLY.getCode().intValue() == mainReversal.getReversalType().intValue()
                || ReversalTypeEnum.APPLY_CANCEL_REFUND.getCode().intValue() == mainReversal.getReversalType().intValue()
                || ReversalTypeEnum.APPLY_CANCEL_NOT_REFUND.getCode().intValue() == mainReversal.getReversalType().intValue())
                && mainReversal.getReversalFeatures() != null
                && (OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode().intValue() == mainReversal.getReversalFeatures().getOrderStatus().intValue()
                || OrderStatusEnum.REVERSAL_DOING.getCode().intValue() == mainReversal.getReversalFeatures().getOrderStatus().intValue()
                || OrderStatusEnum.SELLER_AGREE_CANCEL.getCode().intValue() == mainReversal.getReversalFeatures().getOrderStatus().intValue()
                || OrderStatusEnum.REVERSAL_SUCCESS.getCode().intValue() == mainReversal.getReversalFeatures().getOrderStatus().intValue());
    }
}
