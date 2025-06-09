package com.aliyun.gts.gmall.platform.trade.core.ability.reversal;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalCreateExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.defaultimpl.DefaultReversalCreateExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@Ability(code = "com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalCreateAbility",
        fallback = DefaultReversalCreateExt.class,
        description = "售后单创建扩展能力")
public class ReversalCreateAbility extends BaseAbility<BizCodeEntity, ReversalCreateExt> {

    /**
     * 售后单保存之前
     */
    public void beforeSave(MainReversal reversal) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(reversal.getMainOrder());
        this.executeExt(bizCode,
            extension -> {
                extension.beforeSave(reversal);
                return null;
            },
            ReversalCreateExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    /**
     * 售后单保存之前
     */
    public void fillReversalInfo(CreateReversalRpcReq req,MainReversal reversal) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(reversal.getMainOrder());
        this.executeExt(bizCode,
            extension -> {
                extension.fillReversalInfo(reversal,req);
                return null;
            },
            ReversalCreateExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }
}
