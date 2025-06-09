package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderMessageExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderMessageExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = OrderMessageAbility.ORDER_MESSAGE_ABILITY,
    fallback = DefaultOrderMessageExt.class,
    description = "订单消息发送"
)
public class OrderMessageAbility extends BaseAbility<BizCodeEntity, OrderMessageExt> {

    public static final String ORDER_MESSAGE_ABILITY =
            "com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderMessageAbility";

    public void messageSend(OrderChangeNotify change) {
        BizCodeEntity bizCode;
        if (CollectionUtils.isNotEmpty(change.getOrderList())) {
            bizCode = BizCodeEntity.getDefaultOrderBizCode(change.getOrderList().get(0));
        } else if (change.getMainOrder() != null) {
            bizCode = BizCodeEntity.buildWithDefaultBizCode(change.getMainOrder());
        } else {
            return;
        }
        this.executeExt(bizCode,
            extension -> {
                extension.orderMessageSend(change);
                return null;
            },
            OrderMessageExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    public void messageMqSend(OrderChangeNotify change) {
        BizCodeEntity bizCode;
        if (CollectionUtils.isNotEmpty(change.getOrderList())) {
            bizCode = BizCodeEntity.getDefaultOrderBizCode(change.getOrderList().get(0));
        } else if (change.getMainOrder() != null) {
            bizCode = BizCodeEntity.buildWithDefaultBizCode(change.getMainOrder());
        } else {
            return;
        }
        this.executeExt(bizCode,
            extension -> {
                extension.orderMqMessageSend(change);
                return null;
            },
            OrderMessageExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
