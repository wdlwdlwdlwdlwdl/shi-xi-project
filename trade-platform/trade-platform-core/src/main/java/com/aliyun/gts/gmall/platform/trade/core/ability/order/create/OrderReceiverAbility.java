package com.aliyun.gts.gmall.platform.trade.core.ability.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderReceiverExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderReceiverExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 用户下单地址 扩展点方法
 *
 */
@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderReceiverAbility",
    fallback = DefaultOrderReceiverExt.class,
    description = "下单收货地址校验扩展点"
)
public class OrderReceiverAbility extends BaseAbility<BizCodeEntity, OrderReceiverExt> {

    /**
     * 确认订单 校验收货地点
     *         不用了
     * @param custId
     * @param addr
     * @param order
     * @return
     */
    public TradeBizResult<ReceiveAddr> checkOnConfirmOrder(Long custId, ReceiveAddr addr, CreatingOrder order) {
        // 取第一个bizCode
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) {
            return this.executeExt(
                bizCode,
                extension -> extension.checkOnConfirmOrder(custId, addr),
                OrderReceiverExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        }
        return TradeBizResult.fail(CommonErrorCode.SERVER_ERROR);
    }

    /**
     * 下单 校验收货地点
     * @param custId
     * @param addr
     * @param order
     * @return
     */
    public TradeBizResult<ReceiveAddr> checkOnCreateOrder(Long custId, ReceiveAddr addr, CreatingOrder order) {
        // 取第一个bizCode
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) {
            return this.executeExt(
                bizCode,
                extension -> extension.checkOnCreateOrder(custId, addr),
                OrderReceiverExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        }
        return TradeBizResult.fail(CommonErrorCode.SERVER_ERROR);
    }

    /**
     * 填订单物流方式等信息
     * @param order
     */
    public void fillLogisticsInfo(CreatingOrder order) {
        // 取第一个bizCode
        for (BizCodeEntity bizCode : BizCodeEntity.getOrderBizCode(order)) {
            this.executeExt(bizCode,
                extension -> {
                    extension.fillLogisticsInfo(order);
                    return null;
                },
                OrderReceiverExt.class, Reducers.firstOf(Objects::nonNull)
            );
            return;
        }
    }
}
