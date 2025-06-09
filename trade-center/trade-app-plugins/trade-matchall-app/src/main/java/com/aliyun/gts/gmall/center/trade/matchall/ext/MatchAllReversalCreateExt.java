package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.core.extension.common.CommonReversalCreateExt;
import com.aliyun.gts.gmall.center.trade.matchall.util.MatchAllFilter;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.core.extension.reversal.ReversalCreateExt;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/26 15:43
 */
@Slf4j
@Extension(points = {ReversalCreateExt.class})
public class MatchAllReversalCreateExt extends CommonReversalCreateExt {

    @Override
    public boolean filter(ExtensionFilterContext context) {
        return MatchAllFilter.filter(context);
    }
}
