package com.aliyun.gts.gmall.platform.trade.core.ability.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderCreateEdgeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizRollbackExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderCreateEdgeExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderCreateEdgeAbility",
    fallback = DefaultOrderCreateEdgeExt.class,
    description = ""
)
public class OrderCreateEdgeAbility extends BaseAbility<BizCodeEntity, OrderCreateEdgeExt> {

    /**
     * 开始下单流程, 仅完成了 unpack token
     * @param order
     */
    public void beginCreate(CreatingOrder order) {
        BizCodeEntity bizCode = BizCodeEntity.getOrderBizCode(order).get(0);
        executeExt(bizCode,
            extension -> {
                extension.beginCreate(order);
                return null;
            },
            OrderCreateEdgeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 保存数据库成功
     * @param order
     */
    public void orderSaved(CreatingOrder order) {
        BizCodeEntity bizCode = BizCodeEntity.getOrderBizCode(order).get(0);
        executeExt(
            bizCode,
            extension -> {
                extension.orderSaved(order);
                return null;
            },
            OrderCreateEdgeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 保存数据库之前失败
     */
    public void failedWithoutSave(CreatingOrder order) {
        BizCodeEntity bizCode = BizCodeEntity.getOrderBizCode(order).get(0);
        executeExt(
            bizCode,
            extension -> {
                extension.failedWithoutSave(order);
                return null;
            },
            OrderCreateEdgeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
