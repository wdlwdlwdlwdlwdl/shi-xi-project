package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import java.util.List;
import java.util.Objects;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderStatusChangeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderStatusChangeExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderStatusAbility",
    fallback = DefaultOrderStatusChangeExt.class,
    description = "订单状态变更"
)
public class OrderStatusAbility extends BaseAbility<BizCodeEntity, OrderStatusChangeExt> {

    public TradeBizResult<List<TcOrderDO>> changeStatus(OrderStatus orderStatus, BizCodeEntity bizCode) {
        return this.executeExt(
            bizCode,
            extension -> extension.orderStatusChange(orderStatus),
            OrderStatusChangeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    public Integer getOrderStatusOnPaySuccess(MainOrder mainOrder, OrderPay orderPay) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        return this.executeExt(
            bizCode,
            extension -> extension.getOrderStatusOnPaySuccess(mainOrder, orderPay),
            OrderStatusChangeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
