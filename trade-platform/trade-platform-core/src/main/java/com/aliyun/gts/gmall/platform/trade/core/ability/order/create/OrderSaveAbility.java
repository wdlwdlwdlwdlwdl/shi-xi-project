package com.aliyun.gts.gmall.platform.trade.core.ability.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderSaveExt;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderSaveAbility",
    fallback = OrderSaveExt.class,
    description = "下单订单保存"
)
public class OrderSaveAbility extends BaseAbility<BizCodeEntity, OrderSaveExt> {

    public void saveOrder(CreatingOrder order) {
        BizCodeEntity bizCode = BizCodeEntity.getOrderBizCode(order).get(0);
        // before
        Map context = this.executeExt(
            bizCode,
            extension -> extension.beforeSaveOrder(order),
            OrderSaveExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        // 每个主订单按各自业务身份处理
        for (MainOrder main : order.getMainOrders()) {
            BizCodeEntity mainBizCode = BizCodeEntity.buildWithDefaultBizCode(main);
            this.executeExt(
                mainBizCode,
                extension -> {
                    extension.convertOrder(main, order, context);
                    return null;
                },
                OrderSaveExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        }
        // 保存
        this.executeExt(bizCode,
            extension -> {
                extension.saveOrder(context);
                return null;
            },
            OrderSaveExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        //推送消息到ES中
        this.executeExt(bizCode,
            extension -> {
                extension.pushOrderMessage(order);
                return null;
            },
            OrderSaveExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }


    public void saveOrderForConfirm(CreatingOrder order) {
        BizCodeEntity bizCode = BizCodeEntity.getOrderBizCode(order).get(0);
        // before
        Map context = this.executeExt(
            bizCode,
            extension -> extension.beforeSaveOrder(order),
            OrderSaveExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        // 每个主订单按各自业务身份处理
        for (MainOrder main : order.getMainOrders()) {
            BizCodeEntity mainBizCode = BizCodeEntity.buildWithDefaultBizCode(main);
            this.executeExt(
                mainBizCode,
                extension -> {
                    extension.convertOrderForConfirm(main, order, context);
                    return null;
                },
                OrderSaveExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        }
        // 保存
        this.executeExt(bizCode,
            extension -> {
                extension.saveOrder(context);
                return null;
            },
            OrderSaveExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }


    public void deleteOrderMessage(TcOrderDO orderDO)
    {

        BizCodeEntity bizCode = BizCodeEntity.getDefaultOrderBizCode(orderDO);
        //推送消息到ES中
        this.executeExt(bizCode,
                extension -> {
                    extension.deleteOrderMessage(orderDO.getPrimaryOrderId());
                    return null;
                },
                OrderSaveExt.class,
                Reducers.firstOf(Objects::nonNull)
        );
    }
}
