package com.aliyun.gts.gmall.center.trade.matchall.util;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;

public class MatchAllFilter {

    /**
     * 扩展点的兜底实现, 用此filter
     * 当有多个扩展点实现时, 不执行
     */
    public static boolean filter(ExtensionFilterContext context) {
        return context.getMatchedExtensionPoints().size() <= 1;
    }
}
