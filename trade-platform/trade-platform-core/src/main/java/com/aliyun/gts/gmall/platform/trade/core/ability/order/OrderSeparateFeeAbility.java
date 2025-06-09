package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderSeparateFeeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderSeparateFeeExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderSeparateFeeAbility",
    fallback = DefaultOrderSeparateFeeExt.class,
    description = "分账计算、确认收货金额计算"
)
@Component
public class OrderSeparateFeeAbility extends BaseAbility<BizCodeEntity, OrderSeparateFeeExt> {

    /**
     * 下单时记录分账规则
     */
    public void storeSeparateRule(MainOrder mainOrder) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        this.executeExt(
            bizCode,
            extension -> {
                extension.storeSeparateRule(mainOrder);
                return null;
            },
            OrderSeparateFeeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 记录确认收货价格, 含分账信息
     */
    public void storeConfirmPrice(MainOrder mainOrder) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        this.executeExt(
            bizCode,
            extension -> {
                extension.storeConfirmPrice(mainOrder);
                return null;
            },
            OrderSeparateFeeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}