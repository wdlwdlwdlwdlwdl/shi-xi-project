package com.aliyun.gts.gmall.platform.trade.core.extension.search;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchResult;

public interface ReversalSearchExt extends IExtensionPoints {

    @AbilityExtension(
            code = "REVERSAL_SEARCH",
            name = "售后单搜索",
            description = "售后单搜索"
    )
    ReversalSearchResult search(ReversalSearchQuery query);
}
