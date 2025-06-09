package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderInventoryExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderInventoryExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author haibin.xhb
 * @date 2021/10/21 10:11
 */
@Slf4j
@Ability(
        code = OrderInventoryAbility.ORDER_INVENTORY_ABILITY,
        fallback = DefaultOrderInventoryExt.class,
        description = "订单扣减积分能力"
)
@Component
public class OrderInventoryAbility extends BaseAbility<BizCodeEntity, OrderInventoryExt> {
    public static final String ORDER_INVENTORY_ABILITY =
            "com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility";

    /**
     * 预占库存, 一次传多个主订单时可保证事务, 同时会生成超时解锁任务
     */
    public boolean lockInventory(List<MainOrder> orders) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(orders.get(0));
        Boolean result = this.executeExt(
            bizCode,
            extension -> extension.lockInventory(orders),
            OrderInventoryExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        return result;
    }

    /**
     * 释放预占库存
     */
    public void unlockInventory(List<MainOrder> orders) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(orders.get(0));
        this.executeExt(bizCode,
            extension -> {
                extension.unlockInventory(orders);
                return null;
            },
            OrderInventoryExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 减库存, 要求先预占
     */
    public boolean reduceInventory(List<MainOrder> orders) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(orders.get(0));
        Boolean result = this.executeExt(
            bizCode,
            extension -> extension.reduceInventory(orders),
            OrderInventoryExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        return result;
    }

    /**
     * 回滚库存（非售后逆向的）
     */
    public void rollbackInventoryBeforePay(List<MainOrder> orders) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(orders.get(0));
        this.executeExt(
            bizCode,
            extension -> {
                extension.rollbackInventoryBeforePay(orders);
                return null;
            },
            OrderInventoryExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 回滚库存（非售后逆向的）
     */
    public void rollbackInventoryAfterRefund(MainReversal mainReversal) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainReversal.getMainOrder());
        this.executeExt(bizCode,
            extension -> {
                extension.rollbackInventoryAfterRefund(mainReversal);
                return null;
            },
            OrderInventoryExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
