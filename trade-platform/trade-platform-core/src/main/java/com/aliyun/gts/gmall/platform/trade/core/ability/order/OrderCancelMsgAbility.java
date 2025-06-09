package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderCancelMsgExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderCancelMsgExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = OrderCancelMsgAbility.CANCEL_MESSAGE_ABILITY,
    fallback = DefaultOrderCancelMsgExt.class,
    description = "订单取消消息发送"
)
public class OrderCancelMsgAbility extends BaseAbility<BizCodeEntity, OrderCancelMsgExt> {

    public static final String CANCEL_MESSAGE_ABILITY =
            "com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderCancelMsgAbility";

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
                extension.orderCancelSend(change);
                return null;
            },
            OrderCancelMsgExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
