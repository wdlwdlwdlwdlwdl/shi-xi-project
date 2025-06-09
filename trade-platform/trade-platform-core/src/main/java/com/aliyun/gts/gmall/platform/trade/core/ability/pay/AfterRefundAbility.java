package com.aliyun.gts.gmall.platform.trade.core.ability.pay;


import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.pay.DefaultAfterRefundExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.AfterRefundExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
        code = AfterRefundAbility.AFTER_PAY_ABILITY,
        fallback = DefaultAfterRefundExt.class,
        description = "退款后处理扩展能力"
)
public class AfterRefundAbility extends BaseAbility<BizCodeEntity, AfterRefundExt> {

    public static final String AFTER_PAY_ABILITY =
            "com.aliyun.gts.gmall.platform.trade.core.ability.pay.AfterRefundAbility";

    public TradeBizResult afterRefund(MainReversal mainReversal) {
        BizCodeEntity bizCodeEntity = BizCodeEntity.buildWithDefaultBizCode(mainReversal.getMainOrder());

        return this.executeExt(bizCodeEntity,
                extension -> extension.afterRefund(mainReversal),
                AfterRefundExt.class,
                Reducers.firstOf(Objects::nonNull));
    }

}
