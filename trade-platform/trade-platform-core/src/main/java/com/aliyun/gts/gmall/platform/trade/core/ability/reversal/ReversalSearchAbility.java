package com.aliyun.gts.gmall.platform.trade.core.ability.reversal;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.ReversalSearchExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl.DefaultReversalSearchExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@Ability(code = "com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalSearchAbility",
        fallback = DefaultReversalSearchExt.class,
        description = "售后单搜索能力")
public class ReversalSearchAbility extends BaseAbility<BizCodeEntity, ReversalSearchExt> {

    public ReversalSearchResult search(ReversalSearchQuery query) {
        return executeExt(BizCodeEntity.getFromThreadLocal(),
                extension -> extension.search(query),
                ReversalSearchExt.class,
                Reducers.firstOf(Objects::nonNull)
        );
    }
}
