package com.aliyun.gts.gmall.platform.trade.core.ability.reversal;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalFeeExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl.DefaultReversalFeeExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@Ability(code = "com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalFeeAbility",
        fallback = DefaultReversalFeeExt.class,
        description = "退款金额处理能力")
public class ReversalFeeAbility extends BaseAbility<BizCodeEntity, ReversalFeeExt> {

    public void divideCancelAmt(MainReversal reversal) {
        BizCodeEntity bizCodeEntity = BizCodeEntity.buildWithDefaultBizCode(reversal.getMainOrder());
        this.executeExt(
            bizCodeEntity,
            extension -> {
                extension.divideCancelAmt(reversal);
                return null;
            },
            ReversalFeeExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
