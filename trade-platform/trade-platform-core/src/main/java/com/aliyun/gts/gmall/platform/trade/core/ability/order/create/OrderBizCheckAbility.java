package com.aliyun.gts.gmall.platform.trade.core.ability.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderBizCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCheckAbility",
    fallback = DefaultOrderBizCheckExt.class,
    description = "下单业务检查"
)
public class OrderBizCheckAbility extends BaseAbility<BizCodeEntity, OrderBizCheckExt> {

    /**
     * 订单确认扩展点 --- 订单确认
     * @param order
     * @return TradeBizResult
     * 2024-12-11 14:16:10
     */
    public TradeBizResult checkOnConfirmOrder(CreatingOrder order) {
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) {
            TradeBizResult result = executeExt(
                bizCode,
                extension -> extension.checkOnConfirmOrder(order),
                OrderBizCheckExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
            if (Boolean.FALSE.equals(result.isSuccess())) {
                return result;
            }
        }
        return TradeBizResult.ok(null);
    }

    /**
     * 订单确认扩展点 --- 下单校验
     * @param order
     * @return
     */
    public TradeBizResult checkOnCreateOrder(CreatingOrder order) {
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) {
            TradeBizResult result = executeExt(
                bizCode,
                extension -> extension.checkOnCreateOrder(order),
                OrderBizCheckExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
            if (!result.isSuccess()) {
                return result;
            }
        }
        return TradeBizResult.ok(null);
    }
}
