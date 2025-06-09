package com.aliyun.gts.gmall.platform.trade.core.ability.order.create;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderBizCodeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCodeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCodeExt.OrderBizCode;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCodeExt.OrderBizCodeReq;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility",
    fallback = DefaultOrderBizCodeExt.class,
    description = "订单业务身份识别"
)
public class OrderBizCodeAbility extends BaseAbility<BizCodeEntity, OrderBizCodeExt> {

    /**
     * 商品查询扩展点 -- 用于商品维度在PF4j扩展
     * @param req
     * @return
     */
    public TradeBizResult<OrderBizCode> getBizCodesFromItem(OrderBizCodeReq req) {
        BizCodeEntity bizCode = getCurrentBizCode(req);
        return executeExt(
            bizCode,
            extension -> extension.getBizCodesFromItem(req),
            OrderBizCodeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 营销计算查询扩展点 --- 用于营销维度在PF4j扩展
     * @param req
     * @return
     */
    public TradeBizResult<OrderBizCode> getBizCodesFromPromotion(OrderBizCodeReq req) {
        BizCodeEntity bizCode = getCurrentBizCode(req);
        return executeExt(
            bizCode,
            extension -> extension.getBizCodesFromPromotion(req),
            OrderBizCodeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 获取订单类型码
     * @param req
     * @return
     */
    private BizCodeEntity getCurrentBizCode(OrderBizCodeReq req) {
        for (MainOrder mainOrder : req.getOrder().getMainOrders()) {
            if (CollectionUtils.isNotEmpty(mainOrder.getBizCodes())) {
                return BizCodeEntity.buildWithDefaultBizCode(mainOrder);
            }
        }
        return BizCodeEntity.getFromThreadLocal();
    }
}
