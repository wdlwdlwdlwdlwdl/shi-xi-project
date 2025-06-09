package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultDeliveryStatusChangeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.DeliveryStatusChangeExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.DeliveryStatusAbility",
    fallback = DefaultDeliveryStatusChangeExt.class,
    description = "物流状态变更"
)
public class DeliveryStatusAbility extends BaseAbility<BizCodeEntity, DeliveryStatusChangeExt> {

    public TradeBizResult<List<TcOrderDO>> changeStatus(OrderStatus orderStatus, BizCodeEntity bizCode) {
        return this.executeExt(
            bizCode,
            extension -> extension.orderStatusChange(orderStatus),
            DeliveryStatusChangeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

}
