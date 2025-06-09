package com.aliyun.gts.gmall.platform.trade.core.ability.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderBizRollbackExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizRollbackExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizRollbackAbility",
    fallback = DefaultOrderBizRollbackExt.class,
    description = ""
)
public class OrderBizRollbackAbility extends BaseAbility<BizCodeEntity, OrderBizRollbackExt> {

    public void rollbackBizResource(CreatingOrder order) {
        BizCodeEntity bizCode = BizCodeEntity.getOrderBizCode(order).get(0);
        executeExt(
            bizCode,
            extension -> {
                extension.rollbackBizResource(order);
                return null;
            },
            OrderBizRollbackExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
