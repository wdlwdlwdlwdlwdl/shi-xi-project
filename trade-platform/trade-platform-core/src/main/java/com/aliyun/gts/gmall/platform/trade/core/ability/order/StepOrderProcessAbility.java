package com.aliyun.gts.gmall.platform.trade.core.ability.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.StepOrderHandleRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultStepOrderProcessExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderProcessExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.order.StepOrderProcessAbility",
    fallback = DefaultStepOrderProcessExt.class,
    description = "多阶段推进"
)
public class StepOrderProcessAbility extends BaseAbility<BizCodeEntity, StepOrderProcessExt> {

    public void onPaySuccess(MainOrder mainOrder, OrderPay orderPay) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        this.executeExt(
            bizCode,
            extension -> {
                extension.onPaySuccess(mainOrder, orderPay);
                return null;
            },
            StepOrderProcessExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    public void onSellerHandle(MainOrder mainOrder, StepOrderHandleRpcReq req) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        this.executeExt(
            bizCode,
            extension -> {
                extension.onSellerHandle(mainOrder, req);
                return null;
            },
            StepOrderProcessExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    public void onCustomerConfirm(MainOrder mainOrder, StepOrderHandleRpcReq req) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        this.executeExt(
            bizCode,
            extension -> {
                extension.onCustomerConfirm(mainOrder, req);
                return null;
            },
            StepOrderProcessExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
