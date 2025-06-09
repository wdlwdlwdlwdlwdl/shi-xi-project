package com.aliyun.gts.gmall.platform.trade.core.ability.reversal;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalMessageExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl.DefaultReversalMessageExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalMessageAbility",
    fallback = DefaultReversalMessageExt.class,
    description = "逆向消息发送能力"
)
public class ReversalMessageAbility extends BaseAbility<BizCodeEntity, ReversalMessageExt> {

    public void sendStatusChangedMessage(MainReversal reversal, ReversalStatusEnum targetStatus) {
        List<BizCodeEntity> bizCodes = BizCodeEntity.getOrderBizCode(reversal.getMainOrder());
        for (BizCodeEntity code : bizCodes) {
            executeExt(
                code,
                extension -> {
                    extension.sendStatusChangedMessage(reversal, targetStatus);
                    return null;
                }, ReversalMessageExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        }
    }

    public void autoRefundMessage(MainReversal reversal, ReversalStatusEnum targetStatus) {
        List<BizCodeEntity> bizCodes = BizCodeEntity.getOrderBizCode(reversal.getMainOrder());
        for (BizCodeEntity code : bizCodes) {
            executeExt(
                code,
                extension -> {
                    extension.autoRefundMessage(reversal, targetStatus);
                    return null;
                },
                ReversalMessageExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        }
    }
}
