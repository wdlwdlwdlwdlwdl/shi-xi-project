package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderSuccessExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderSuccessExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderSuccessAbility",
    fallback = DefaultOrderSuccessExt.class,
    description = "订单售中完成"
)
public class OrderSuccessAbility extends BaseAbility<BizCodeEntity, OrderSuccessExt> {

    /**
     * 订单售中完成处理, 发送ORDER_SUCCESS 消息之前, 记录确认收货金额、分账金额到DB
     */
    public void processOrderSuccess(MainOrder mainOrder) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        this.executeExt(bizCode,
            extension -> {
                extension.processOrderSuccess(mainOrder);
                return null;
            },
            OrderSuccessExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
